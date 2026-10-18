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

### Function regression checks

After compiling and packaging the application (`mvn package`), these
dependency-free checks can be run from the repository root in PowerShell:

```powershell
New-Item -ItemType Directory -Force target\linalg-tests | Out-Null
javac -cp 'target\classes;target\XWizard\WEB-INF\lib\*' -d target\linalg-tests tests\LinAlgFunctionsTest.java
java -cp 'target\linalg-tests;target\classes;target\XWizard\WEB-INF\lib\*' LinAlgFunctionsTest
```
