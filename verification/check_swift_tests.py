"""Require the four real Anstyle Swift Testing cases and no skipped cases."""
import re
import sys
from pathlib import Path

text = re.sub(r"\x1b\[[0-9;]*m", "", Path(sys.argv[1]).read_text())
if not re.search(r"Test run with 4 tests[^\r\n]*passed", text):
    raise SystemExit("Expected all four Anstyle Swift tests to pass")
if re.search(r"(?m)^.*(?:Test|Suite) .*\b(?:failed|skipped)\b", text):
    raise SystemExit("Anstyle Swift test failed or was skipped")

for name in [
    "ansiPaletteConversion",
    "rgbComponents",
    "styleEffects",
    "effectsIdentityConversion",
]:
    label = r'(?:' + re.escape(name) + r'\(\))'
    alias = r"(?: \(aka '" + re.escape(name) + r"\(\)'\))?"
    pattern = r"Test " + label + alias + r" passed\b"
    if not re.search(pattern, text):
        # Also check without parens
        if not re.search(r"Test " + re.escape(name) + r"\b.*passed\b", text):
            raise SystemExit("Expected passing execution of " + name)

# Package.swift configuration commands and SwiftPM's generated runner are
# distinct from the dependency and authored-test frontend invocations.
import json
frontends = []
for line in text.splitlines():
    if 'swift-frontend' not in line or '-module-name ' not in line:
        continue
    module = re.findall(r'-module-name\s+(\S+)', line)[-1]
    if module == 'main' and 'Package.swift' in line:
        continue
    modes = re.findall(r'-swift-version\s+(\S+)', line)
    mode = modes[-1] if modes else None
    if '-warnings-as-errors' not in line or not re.search(r'-target\s+arm64-apple-macos(?:x)?15\.0', line):
        raise SystemExit('Missing Swift warning/deployment gate: ' + module)
    if module == 'SwiftTestHarnessTests':
        if mode not in ['6', '6.0']:
            raise SystemExit('Authored Swift consumer must compile in mode 6')
    elif module == 'SwiftTestHarnessPackageTests':
        if mode not in [None, '6', '6.0']:
            raise SystemExit('Unexpected generated test-runner mode')
    elif mode not in ['5', '5.0']:
        raise SystemExit('Published generated dependencies require mode 5: ' + module)
    frontends.append({'module': module, 'swift_language_mode': mode,
                      'warnings_as_errors': True, 'macos_deployment_target': '15.0'})
required = {'Anstyle', 'KotlinRuntimeSupport', 'KotlinCoroutineSupport', 'SwiftTestHarnessTests'}
if not required <= {row['module'] for row in frontends}:
    raise SystemExit('Missing actual dependency/consumer frontend evidence: ' + str(required - {row['module'] for row in frontends}))
Path(sys.argv[1]).with_suffix('.json').write_text(json.dumps({
    'actual_behavior_tests': 4, 'failures': 0, 'skips': 0,
    'frontends': frontends, 'generated_runner_classified_separately': True,
}, indent=2) + '\n')
