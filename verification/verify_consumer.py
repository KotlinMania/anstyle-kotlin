"""Verify actual consumer execution, variant selection and staged payload identity."""
import argparse
import hashlib
import json
import xml.etree.ElementTree as ET
from pathlib import Path


def verify(fixture, repository, version):
    reports = fixture / 'build/reports/publication'
    variant = json.loads((reports / 'selected-variant.json').read_text())
    assert (variant['group'], variant['module'], variant['version']) == (
        'io.github.kotlinmania', 'anstyle-kotlin-android', version), variant
    attrs = variant['attributes']
    assert attrs['org.jetbrains.kotlin.platform.type'] == 'androidJvm'
    assert attrs['org.gradle.libraryelements'] == 'aar'
    assert attrs['org.gradle.jvm.environment'] == 'android'
    duplicate = json.loads((reports / 'duplicates.json').read_text())
    assert duplicate['duplicates_by_configuration'] == {'compile': {}, 'runtime': {}}
    tests = []
    for task in ['testAndroidHostTest', 'jvmTest']:
        cases = []
        for path in (fixture / 'build/test-results' / task).glob('*.xml'):
            root = ET.parse(path).getroot()
            assert not root.findall('.//failure') + root.findall('.//error') + root.findall('.//skipped')
            cases.extend(c.get('classname', '') + '.' + c.get('name', '') for c in root.findall('.//testcase'))
        assert len(cases) == 4, (task, cases)
        tests.append({'task': task, 'cases': cases, 'executed': len(cases), 'failures': 0, 'errors': 0, 'skipped': 0})
    selected = {}
    for platform, report, extension in [('android', 'raw-classpaths.json', 'aar'), ('jvm', 'jvm-classpaths.json', 'jar')]:
        paths = json.loads((reports / report).read_text())
        module = 'anstyle-kotlin-' + platform
        artifact = repository / 'io/github/kotlinmania' / module / version / (module + '-' + version + '.' + extension)
        expected = hashlib.sha256(artifact.read_bytes()).hexdigest()
        selected[platform] = {}
        for scope in ['compile', 'runtime']:
            matches = sorted(set(p for p in paths[scope] if '/' + module + '/' + version + '/' in p and p.endswith('.' + extension)))
            assert len(matches) == 1, (platform, scope, matches)
            assert hashlib.sha256(Path(matches[0]).read_bytes()).hexdigest() == expected
            selected[platform][scope] = {'path': matches[0], 'sha256': expected}
    return {'status': 'passed', 'version': version, 'availability': 'Filesystem staging only; not Central publication',
            'tests': tests, 'selected_variant': variant, 'selected_payloads': selected,
            'duplicates_by_configuration': duplicate['duplicates_by_configuration']}


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--fixture', type=Path, required=True)
    parser.add_argument('--repository', type=Path, required=True)
    parser.add_argument('--version', required=True)
    parser.add_argument('--receipt', type=Path, required=True)
    args = parser.parse_args()
    result = verify(args.fixture, args.repository, args.version)
    args.receipt.parent.mkdir(parents=True, exist_ok=True)
    args.receipt.write_text(json.dumps(result, indent=2) + '\n')
    print('PASS: four Android host and four JVM cases, selected payload hashes and independent classpaths')
