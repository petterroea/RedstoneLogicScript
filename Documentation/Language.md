#The Redstone Logic Script language

## Abstract

The Redstone Logic Script language(RLS) exists due to a need to quickly and efficiently write logical statements that translate into redstone circuitry for the video game Minecraft. As a result of this, limitations in the language are often a result of practical limitations in redstone.

The industry standard language verilog is often used as a comparison. However, the language is not based off verilog, and only shares the inspiration and wish to abstract away physical circuitry through logical statements.

## The file type

RLS files are stored using the extension `.rls`. An RLS file may contain the following different things:

 * Comments - a comment is single-line, and is marked using the character `#`.
 * Includes - an RLS file may request that the compiler parses other files before proceeding with the current file. This is used to load modules from the standard library, or other modules in the users current project
 * Modules - a module is the building brick of an RLS design. Modules are recursively used to specify logical connections between each others, all the way down to modules which only import a minecraft structure representing a logical gate. It is considered good to keep to only one module per file for important functionality. However, helper-modules may be grouped together if they exist in large numbers. If so, the file name should reflect the type of helper modules residing in the file.
 
### Example file

```
#First RLS file
#Example: Hello world
#Author: petterroea

include "basic-logic-gates.rls";

module Helloworld !main{
	input a;
	input b;
	input c;
	
	internal Or or;
	
	or.a = a;
	or.b = b;
	
	internal point v;
	
	v = a & b;
	
	output orOut;
	output out;	
	
	orOut = or.out;
	out = v & c;
}
```
 
## Includes

Includes tell the compiler to parse another file before continuing on with the current file. Files will not be parsed twice, which is something the compiler takes care of for you. When searching for the specified file, the compiler first searches for the file in the current working directory. If not, it will do a shallow search through each folder specified as a library folder via command line. A normal mistake may be to forget to include the basic component library when compiling your own project.

## Comments

Comments may start anywhere on a line, and last until the end of said line. Text in a comment is ignored by the compiler.

## Modules

Modules are the heart and soul of a RSL project. Modules define the logical connection between internal child modules, which again define the logical connection between their children. This continues recursively until the only children are modules with models. A model is a minecraft structure that represents a logical gate. During compilation, modules are assembled from the lowest level modules where their children are models, and upwards torwards the main module.

 * **TIP**: A Model is a minecraft structure of a logical gate. During compilation, a module without models is built, wired, and becomes a model-containing module.
 
Modules are comprised of internals, inputs, and outputs. Using expressions, you can connect the inputs and outputs to the internals. You do this to build functionality, layer after layer of modules. By doing this, you encapsulate the internal state of the module, and expose only the useful bits using inputs and outputs.
 
### Inputs and outputs

Inputs and outputs are public-facing connection points of a module, and should be pretty explanatory. The state of a module is manipulated using inputs, and the state can be read using outputs. The syntax for defining an input or output is:

```
input a;
output b;
```

The previous code defines two public-facing points, the input a, and the output b. A connection point may be named anything which is strictly alphanumeric. These points are the only logical values a containing module may read or write to.

### Internals

An internal can be one of two things

 * A common connection point shared by multiple outputs, inputs, and internal modules
 * An internal module used to provide parts of the logical behavior of the containing module.
 
For internal connection points, the behavior is pretty straight forward: it's a way of connecting a multitude of points together in a very obvious way, and should always be preferred in order to increase readability.

Internal modules are a slightly different story. Internal modules can be seen as modules "hidden from the public". The state of a module is the public-facing results of a network of connected internal modules, which do the actual lifting work. 

### Operators

RSL allows you to define your own operators. These act as a short hand form for creating an internal module and assigning connections. Please note that some operators are already defined by the standard library. An operator is defined like this:

```
operator & a,b: out;

```

The `operator` keyword takes in the operator name(maximum length of two characters), which cannot be alphanumeric. Then, it takes in the two inputs on the module that the left and right hand sign should be assigned to. Last, it takes the output of the module that the result of the operator should be assigned to. In the example above, we define an operator `&`, connecting the left hand to the modules input `a`, and connecting the right hand to the input `b`. The result of the operator can be fetched from an output named `out` on the module.

An operator can only be used in the right hand side of an expression, and is converted into an internal module compile-time. For example, given the following code:

```
module And {
	input a;
	input b;
	output out;
	operator & a,b:out;
	#actual module logic here
}
```

The following snippets are identical:

```

input c;
input d;
output result;
result = c & d;
```

```
input c;
input d;
output result;
#Begin operator replacement here
internal And andGate;
andGate.a = c;
andGate.b = d;
result = andGate.out;
```

In fact, this is exactly what happens during compilation. The names of the modules are randomized using md5 hash, so any compiler log entries with random names are most likely unwrapped operators from this process.