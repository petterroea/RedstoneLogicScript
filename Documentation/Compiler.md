# Compiler documentation

The compiler consists of 4 different phases, each with a purpose. On the highest level, the language is a low level digital logic language, with few abstractions. In fact, the "minecraft compiler" part of it is implemented in its own stage, and can be replaced with another generator if needed. 

The following are the 4 stages of the compiler:

1. Parsing
2. Validation
3. Block generation
  - Module placement
  - Netlist generation
  - Netlist placement
4. `.schematic` generation

As you can see, only stage 1 and 2 are actually related to the language. 

This documentation is written with the expectation of knowledge of RLS. Please read the `Language.md` document first. Read this to learn about the inner workings of the compiler, which may be useful for further development of the compiler.

## Parsing

The compiler starts by parsing the main module. From here, it recursively parses imported files as they are defined, until everything is parsed.

The main parsing routine is `parseFile`, which exists in `Compiler.java`, and it works by looking for supported global keywords. The current supported keywords are `module` and `import`. If it encounters something else, it will fail. However, if it does encounter one of these keywords, it will call on code which is responsible for parsing the keyword from beginning to end. Contrary to naming, some early verification happens here, such as naming conflict errors.

### `import` 

`import` is handled inline. It calls `parseFile` on the specified file, if it is not already imported.

### `module`

`module` is parsed by function `ModuleParser.parseModule`. It takes the responsibility of parsing the module data structure from `{` to `}`. This also includes parsing models, whose function also resides in `ModuleParser.java`. Operators are parsed and registered at this point. Operators work as short-hand macros for a module, and are registered in `CompilerState.java`.

## Validation

The validation stage is responsible for making sure that all expressions are valid. This has to be done in a seperate stage to allow for expressions to exist before parts of them have been defined, a nice feature. Validation is done by `Module.validateExpressions`.

When this stage is finished, a 100% valid object-oriented representation of the written code exists in memory.

## Block generation

This stage works recursively on all children of a module. The recursion works down the dependency tree until it hits modules with only model-modules(modules with a pre-defined model defining its look and redstone behavior) as children. The blocks from these, as well as their connection points, are added to the modules internal list. The block placement algorithm itself is random, it places one block, and then tries best-effort to place the rest of the modules in close proximity to where they are to be connected to.

The netlist is TODO.