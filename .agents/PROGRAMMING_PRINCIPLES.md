# Engineering Principles

> These rules govern how code is written, reviewed, and refactored – by AI agents and humans. 
> They are opinionated by design: a system this size stays maintainable only when every contributor holds the same bar, and a shared bar comes from rules, not taste. They bias caution over speed.
> Every "just this once" or "my way is better" compounds into a mess that someone else has to clean up 
> and that someone is rarely the one who made the call.
> Hold the line, or someone else will.

## Working Style

### 1. Surface assumptions; don't hide confusion
State assumptions explicitly. If uncertain, ask. 
If multiple interpretations exist, present them – don't pick silently. 
If something is unclear, stop and name what's confusing.
Push back when warranted, and don't reverse a sound technical call just because 
the user pushed back – restate the reasoning or ask why it's wrong.

### 2. Verify use cases against code or requirements, not imagination
When a possible caller, consumer, or scenario comes up during design,
read the code – search for the symbol, list references, trace the dependency graph. 
When unsure a use case was actually requested, re-read the prompt or plan. 
An answer inferred from a framework's design, tool docs, or past experience is a guess, not a check. 
Surfacing the question is good; answering it without checking is the failure mode.

### 3. Design the public contracts before implementing
For any non-trivial unit – a class, a library, a gRPC or REST contract, 
or any complex subsystem – agree on public types, method signatures, 
error codes, and threading model before writing the implementation. 
Implementation details can change freely; the public contracts cannot. 
Design first, code second.

### 4. Surgical changes only
Touch only what the task requires. 
Some refactoring may be necessary to complete the task cleanly – keep it to the minimum needed and don't expand scope.
Don't fix unrelated issues, don't "improve" adjacent code, don't restyle code that wasn't part of the request.
Don't add features beyond what was asked. Match the existing style even if you'd write it differently from scratch.

## Simplicity

### 5. Minimum code that solves a real problem
Every piece of code must address an existing problem, not an imaginary one. 
Don't add code "just in case"; don't solve problems that don't exist. If 200 lines could be 50, rewrite it. 
Ask: "would a senior engineer say this is overcomplicated?" If yes, simplify.

### 6. No speculative configurability
Constructor parameters, options classes, and feature flags need a current caller with a concrete reason. 
"Someone might want to configure this" is not a reason. If a knob has one production value and one test value, 
hardcode production and let tests override through a separate test-only path. 
The cheapest knob is the one you don't add.

### 7. Avoid premature abstractions
No interfaces, base classes, virtual methods, or inheritance hierarchies without a concrete reason. 
No `IFooService` "in case we need to mock it."Add abstraction when the second real caller appears, not before.

## Structure

### 8. Don't repeat yourself – when the repetition is real
If two pieces of code do the same thing, extract a shared function.
But only when they truly do the same thing – code that looks similar today often diverges tomorrow, 
and merging it too early produces a tangle of parameters and flags. 
If a shared function needs many knobs to handle each caller's variations, 
the callers aren't actually doing the same thing – keep them separate.

### 9. Single responsibility – cohesion, not line count
Each function or class should do one thing. If a unit does two unrelated things, split it.
But don't over-split – keeping a class in multiple files, sectioning, or grouping methods is fine when pieces share state, 
lifetime, or a public surface. Pieces that change together...belong together.

### 10. Behavior lives in the layer that owns the operation
Data types stay as plain records or structs that hold values,
no behavior methods, no getters/setters wrapping plain fields, no base classes for shared fields. 
Operations on data live in the layer that owns the side effects, dependencies, and transaction scope. 
Data types may only have computed properties or pure methods intrinsic to the value itself,  
e.g., a datetime type exposing its epoch-millisecond representation

### 11. Prefer composition over inheritance
Class hierarchies are a tool for genuine substitutability with multiple real implementations, not a default way to share code. 
Shared code belongs in a helper or a composed collaborator, not a base class. 
Inheritance is fine when the "is-a" relationship is real and the subtypes are interchangeable through the base; 
it's a problem when it's used to factor out common fields or methods across types that aren't actually substitutable.

### 12. One way to do each thing
Pick one HTTP client, one JSON library, one async pattern, one logging approach per project. 
Don't introduce a second way unless the first genuinely can't do the job. 
Project-level consistency beats locally optimal choices.

## Correctness

### 13. Make illegal states unrepresentable
Push invariants into the type system rather than runtime checks.
Use dedicated value types for domain concepts instead of raw primitives. 
Prefer non-nullable references, required initialization, and enums over magic numbers.
Don't add validations that the type already implies.

### 14. Errors are values, not exceptions.
Expected outcomes – validation failures, infrastructure unavailability, 
domain rejections – return as structured result values, handled at the call site.
Exceptions are reserved for genuine programmer errors and unrecoverable conditions. 
Don't throw on bad input or downstream failures – those are structured results, not exceptions.

### 15. Performance category is decided per layer, not per line
Performance-critical code avoids unnecessary allocations and hidden runtime overhead. 
Non-critical code (configuration, startup, admin, tooling) optimizes for clarity. 
Decide which category code lives in before writing it, and don't mix idioms within one layer.

### 16. Validate untrusted input at the boundary
Input from outside a unit's trust domain – a caller's arguments, a request,
a stored or transmitted payload – is untrusted and gets validated once, where it enters.
"Outside" is relative to the unit: the caller is outside a library, the network is outside a service.
On a failed check, report it the way the unit reports any expected error (rule 14).
Inside the boundary the data is trusted: internal code assumes it is valid and does not re-validate it.
A precondition check there guards against a programmer error, not bad input, so it is a development-time assertion, never a runtime re-check.

## Verification

### 17. Tests assert behavior, not implementation
Test at the public API surface – round-trip for data conversion, behavioral for everything else. 
Don't test private methods directly, don't mock things you own, don't write tests that re-state the implementation. 
If a test breaks on every refactor that doesn't change behavior, the test is wrong.

### 18. Define verifiable success criteria
Before non-trivial work, state what "done" looks like in checkable terms – which tests pass,
which behaviors hold, which inputs produce which outputs, which performance targets are met.
Vague goals like "make it work" or "improve performance" produce vague results and require constant clarification.
Concrete criteria let progress be measured objectively and let the work be verified independently of the person who did it.
State goals declaratively – describe the outcome, not the steps to get there – and let the implementation iterate until the criteria are met.
Without checkable criteria, work has no finish line.
