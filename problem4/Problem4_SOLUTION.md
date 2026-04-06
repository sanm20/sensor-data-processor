# Problem 4 — CrackMe Solution

**Team 14 | SE-4900 Learning Sprint #3 | Iowa State University**

---

## The Result

We successfully identified the input that unlocks the program and prints the key message. Manual tracing of the CrackMe logic revealed that the program validates input through a series of mathematical constraints applied to the ASCII values of the input characters. The core check is a modular arithmetic condition where the sum of character ASCII values mod a prime constant must equal a specific residue. Combined with positional constraints (specific indices must satisfy character class conditions — e.g., index 0 must be uppercase alpha, certain indices must be digits), the valid input space was small enough to solve algebraically.

By extracting the exact constant values from the source code through manual tracing and setting up the constraints as a system of equations, we identified a valid input string without brute-forcing. A screenshot of the successful program run printing the key is in this repository at `/problem4/screenshot.png`.

---

## Reflection

Human intervention was essential at two points. First, we simplified the program by commenting out all output formatting and intermediate print statements, leaving only the constraint-checking logic visible. Without this simplification, the control flow was obscured by noise and it was difficult to see the dependency order between constraints. Second, we restructured the interleaved constraint checks into a single annotated pseudocode document, which made the dependency graph explicit — the original code evaluated constraints in an order that was not the same as the logical order in which they needed to be solved.

The AI hallucinated on this problem in a specific and predictable way. It correctly identified the high-level structure ("this is a character-sum modular arithmetic check with positional constraints") but then fabricated specific constant values that did not appear anywhere in the source code. When asked to solve the constraints numerically using those invented constants, it produced a plausible-looking input string that failed immediately at runtime. Once we provided the exact constants extracted through our manual trace — rather than asking the AI to read them itself — it produced a correct constraint solution on the next attempt. This was a clear case where AI was useful as a reasoning partner after the hard extraction work was done manually, but could not be trusted to extract ground-truth values from code reliably.

The interaction between human intelligence and AI on this problem followed a clean division of labor: humans handled the low-level precision work (reading exact constants, simplifying the program, structuring the constraint system), while AI handled the higher-level reasoning once the problem was well-specified. Neither alone would have been as efficient. The lesson is that for reverse-engineering tasks, AI is better used as a solver once the problem is fully specified rather than as a reader of the original code.

---

*Screenshot location: `/problem4/screenshot.png`*
