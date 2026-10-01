## Kanama fork (falcon4ever/PanamaPort)

This fork is upstream PanamaPort plus a short list of patches that the
[Kanama](https://github.com/falcon4ever/kanama) Godot runtime needs. It is published through JitPack as
`com.github.falcon4ever.PanamaPort:Core:<tag>`. The current tag is **`0.1.5-kanama-r8.1`**, which is upstream
**`v0.1.5`** plus the patches below. Kanama rebases the fork onto each upstream release rather than letting it
drift. Before every Kanama release, the fork's upstream base is compared with upstream's latest release.

| Patch | Why | In upstream? |
|---|---|---|
| Sealed-type `switch`es rewritten as `instanceof` chains, plus R8 annotations | Godot's R8 (AGP 8.6.1) miscompiles the pattern switches into `shouldNotReachHere` in minified release builds. | no (v0.1.5) |
| JitPack publishing: a single release variant, and an unsigned local publish | JitPack serves the classified multi-variant AARs as 404s. | no |
| `ArtVersion.SDK_INT_FULL_COMPAT` | Upstream reads the Android-16-only `SDK_INT_FULL` field unconditionally, so every device below Android 16 fails the bootstrap with `NoSuchFieldError`. | no (v0.1.5) |
| `NativeCodeBlob.makeCodeBlobSingle` | R8 turns a one-element `byte[]...` call into `filled-new-array [[B`, which crashes ART's interpreter below Android 13. | no (v0.1.5) |
| `compileSdk` kept at 36 | Upstream's 37 writes `minCompileSdk=37` into the AARs, and Godot 4.7.2's export template (compileSdk 36) rejects that. | n/a (fork-only) |

**Rebase checklist:**
1. Fetch the upstream tags.
2. Re-apply each patch onto the new upstream tag, and drop any patch upstream now covers.
3. Build with JDK 21, publish to mavenLocal under a `-test` version, and point Kanama at it with
   `-PkanamaPanamaPortCore=<coordinate>`.
4. Prove the build on a device: Kanama's nine-demo Android matrix and the R8-minified Match3 release.
5. Tag `<upstream>-kanama-r8.N` and let JitPack build it.

## About

`PanamaPort` is a library implementing the [Foreign Function & Memory API](https://openjdk.org/jeps/454) for Android 8.0+ (API level 26)

### Components

This project contains 4 sublibraries:

- Core - the entire public API and implementation of the Panama project
- Unsafe - low-level implementation details and additional capabilities not provided by the original API
- LLVM - bindings to the built-in libLLVM.so Android library
- VarHandles - backport of java.lang.invoke.VarHandle which didn't exist in android 8.x

### Requirements

- JDK 21+
- Gradle 9.3.1+
- Android Gradle plugin 9.1.1+
- compileSdk 37+

Using versions lower than those listed may cause compilation and runtime issues.
Older versions of R8/D8 have known bugs that prevent the library from working properly

### Get started

Just add this library to the list of dependencies:

```
dependencies {
    implementation 'io.github.vova7878.panama:Core:<version>'
}
```

---

### Supported features

PanamaPort implements full Foreign Function & Memory API support, including:

- Downcall handles - allows Java code to call foreign functions
- Upcall stubs - allows foreign functions to call Java method handles
- Linker options such as captureCallState("errno") and even critical(allowHeapAccess: true)
- Access to native memory via VarHandles
- Management of memory segments via Arenas
- And much more

### Differences from the original API

- Package java.lang.foreign moved to com.v7878.foreign package because it is a port and not part of the Java core library
- WrongThreadException class is also in the com.v7878.foreign package because it doesn't exist on android
- My implementation of VarHandle is used instead of java.lang.invoke.VarHandle

### What was added

All non-standard apis added to the port are marked with `@PortAPI` annotation

This includes just useful methods:

- `MemoryLayout.valueLayout(Class<?>, ByteOrder)`
- `MemoryLayout.sequenceLayout(MemoryLayout)`
- `MemoryLayout.paddedStructLayout(MemoryLayout...)`
- `MemorySegment.nativeAddress()`

Android-specific linker options:

- `Option.allowExceptions()` and `Option.JNIEnvArg(int)`

Port of methods outside the java.lang.foreign package:

- `FileChannelUtils.map(FileChannel, MapMode, long, long, Arena)`

### Interaction with jextract

Usually, to use the output of jextract, it is enough to fix package names. If something doesn't work properly, please create an issue

### Examples and tests

Examples are available in [this](https://github.com/vova7878/PanamaExamples) repository. The same repository is where tests are ported from the OpenJ9 and Hotspot implementations
