# Building array4j with Intel MKL on Linux #

The following SConstruct should be useful:

```
env = Environment()
env.Replace(
    CCFLAGS=['-Wall', '-O2', '-fPIC'],
    CPPPATH=['/opt/intel/mkl/9.1/include'],
    LIBPATH=['/opt/intel/mkl/9.1/lib/32'],
    LIBS=['mkl', 'libguide'],
    LINKFLAGS=['-Wl,-rpath,/opt/intel/mkl/9.1/lib/32'])
env.SharedLibrary('array4j', ['array4j.c'])
```

