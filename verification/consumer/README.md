# Staged Anstyle consumer

This independent build uses only `io.github.kotlinmania:anstyle-kotlin` and Kotlin test. The root/Android/JVM coordinates resolve exclusively from the required `producerRepository` property; there is no source substitution or repository fallback for those modules.

From the producer root, after staging the three publications:

```bash
./gradlew -p verification/consumer compileAndroidMain compileAndroidHostTest testAndroidHostTest jvmTest \
  -PproducerVersion=0.1.6 -PproducerRepository="$PWD/build/staging-deploy" \
  --no-daemon --max-workers=2 --no-configuration-cache --console=plain \
  -Pkotlin.compiler.execution.strategy=in-process
python3 verification/verify_consumer.py --fixture verification/consumer \
  --repository build/staging-deploy --version 0.1.6 \
  --receipt build/reports/verification/consumer.json
```

Set `ANDROID_HOME` and `ANDROID_SDK_ROOT` to the same existing SDK. The five named tests execute separately on Android host and JVM. They compile all six concrete companion types, test color conversions/rendering, preserve immutable style aliases, check effect conversion, and verify full-scalar parser callbacks plus invalid UTF-8 recovery. The verifier requires all ten cases without skips, the actual Android API AAR variant, independently duplicate-free compile/runtime classpaths, and exact selected Android/JVM hashes matching staging.

This proves the staged Android/JVM consumer contract. It does not publish the staged version, execute Android device/native tests, establish complete Rust workspace parity, or establish a Swift pass. The strict producer root gate remains the release gate.
