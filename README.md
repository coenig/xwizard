# XWizard
An online computer science tool for learning and teaching.

## Linear algebra scripts

`linalg:` scripts contain named matrices, column vectors and expressions. Matrix
rows are separated by semicolons; vector components by commas. For example:

```text
linalg:
A = [1 2 3; 2 4 6; 1 1 1]
u = (1, 2, 1)
v = (2, 4, 1)
det(A)
rank(A)
span(u, v)
R = rref(A)
N = nullspace(A)
Z = A * N
d = det(A) + rank(A)
svd(A)
eig(A)
```

Function calls stay in the script: results are computed for display, not
substituted into the source. Numeric/matrix functions work both as standalone
lines and inside assignments or nested expressions.
Lowercase `det` calls show one first-row Laplace expansion before the result, for
every square matrix of size two or larger:
`det(A) = a11 * det(M11) - a12 * det(M12) + ... = result`.
Each minor is displayed as a matrix with the first row and the corresponding
column removed; its determinant is not expanded further. A 1-by-1 determinant
shows its sole entry directly. The same expansion appears for `d = det(A)`;
calls nested in other expressions remain concise. Traces of size up to four
and norms with up to four entries also show a substituted formula.

| Function | Result |
| --- | --- |
| `det(A)` | Determinant of a square matrix |
| `rank(A)` | Number of independent columns |
| `trace(A)` | Sum of the diagonal of a square matrix |
| `norm(A)` | Euclidean norm for vectors; Frobenius norm for matrices |
| `rref(A)` | Reduced row echelon form |
| `basis(A)` | Independent original columns, as a basis matrix for the column space |
| `nullspace(A)` | Basis matrix for the kernel, with one basis vector per column |
| `transpose(A)` | Transpose, also written `A^T` |
| `inv(A)` | Inverse of a nonsingular square matrix, also written `A^-1` |

Capitalize a function's first letter to request an algorithm explanation before
its result: `Det(A)`, `Rank(A)`, `Trace(A)`, `Norm(A)`, `Rref(A)`, `Basis(A)`,
`Nullspace(A)`, `Inv(A)`, `Transpose(A)`, `Span(A)`, `Svd(A)`, `Eig(A)`.
Elimination-based calls show actual row operations and intermediate matrices;
the others describe their construction or decomposition algorithm.
Numeric/matrix variants also work in assignments and nested expressions, e.g.
`d = Det(A) + Rank(A)`. `Span`, `Svd`, and `Eig` remain display-only.
Capitalization does not change the numeric result or rewrite the source.
`Det(A)` shows Gaussian elimination followed by the result, without the Laplace
expansion used by lowercase `det(A)`.

`span(A)` displays the subspace spanned by A's columns, using an independent
generating set. `span(u, v, ...)` accepts column vectors of equal dimension.
Like `svd(A)` and `eig(A)`, `span(...)` is display-only and must stand on its own
line. Use `basis(A)` when you want a matrix you can reuse in calculations.
The span of zero vectors is displayed as `{0}`. A trivial kernel is represented
by an n-by-0 basis matrix, not by a zero basis vector.

Rank, row reduction and basis calculations use partial pivoting with a relative
tolerance of `1e-12`; nearly dependent columns may therefore be treated as dependent.
Invalid dimensions, unknown functions and invalid arguments report a line-numbered
error. Legacy `matrix:` scripts with whitespace-separated entries remain accepted.

### Scalar functions and gradients

Define a real scalar function using named parameters, then evaluate its symbolic
gradient at a column-vector point:

```text
linalg:
f(x, y) = x^2 + 2*x*y + 3*y^2
p = (1, 2)
g = grad(f, p)
Grad(f, p)
```

Here `g` is the column vector `(6, 14)`. `Grad` additionally displays the symbolic
partial derivatives (in parameter order), the evaluation point and the result.
Both forms use symbolic differentiation, not finite differences. Gradients can be
used in matrix expressions, e.g. `w = A * grad(f, p)`.

Scalar-function bodies support `+`, `-`, `*`, `/`, powers `^`, parentheses,
`sin`, `cos`, `exp`, and natural logarithm `log`. Powers are right-associative;
`-x^2` means `-(x^2)`. Only declared parameters and numeric constants can be used
in a body; functions cannot capture scene objects or call other user functions.
Definitions must precede their use. Function names and parameter names are
case-sensitive; built-in function names are reserved. These definitions currently
serve gradient calls; direct scalar-function evaluation is not yet supported.

The point must have one component per parameter; for one variable use a 1-by-1
column matrix such as `grad(f, [2])`. Invalid domains, non-finite results, and
dimension mismatches give line-numbered errors. Derivatives are real-valued
where the differentiation rules apply; non-finite derivative values are rejected.

With `showPlot=true`, an assigned gradient such as `g = grad(f, p)` is drawn
from `p` to `p + g` in 2D/3D. It remains an ordinary column vector in calculations.
Aliases of `g` retain the anchor; newly computed vectors such as `A * g` are
drawn from the origin. Standalone gradient calls display formulas but are not
added to the plot. Contours and gradient fields are not implemented yet.

### Function regression checks

After compiling and packaging the application (`mvn package`), these
dependency-free checks can be run from the repository root in PowerShell:

```powershell
New-Item -ItemType Directory -Force target\linalg-tests | Out-Null
javac -cp 'target\classes;target\XWizard\WEB-INF\lib\*' -d target\linalg-tests tests\LinAlgFunctionsTest.java
java -cp 'target\linalg-tests;target\classes;target\XWizard\WEB-INF\lib\*' LinAlgFunctionsTest
```
