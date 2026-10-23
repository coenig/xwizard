# XWizard
An online computer science tool for learning and teaching.

## Graphviz rendering failures

Graphviz rendering checks the `dot` exit status and requires nonempty output
before reading SVG or PDF bytes. Failures report the executable, exit code and
combined renderer diagnostics (bounded to 8 KiB) instead of a misleading
`IndexOutOfBoundsException` from reading an empty temporary SVG. A missing
executable preserves its I/O exception as the cause. Temporary DOT, output and
diagnostic files are cleaned up on both success and failure.

For a Docker deployment, rebuild/redeploy the WAR and application image to obtain
these diagnostics, then reproduce the failing script. Check the reported `dot`
error for invalid DOT, unavailable format plugins, permissions or resource
failures; an empty file by itself does not identify which occurred.
The container config selects `/usr/bin/dot`. Check `dot -V` and render a minimal
graph with `dot -Tsvg` inside the affected container to distinguish installation
problems from script-specific failures.

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
evd(A)
```

Function calls stay in the script: results are computed for display, not
substituted into the source. Numeric/matrix functions work both as standalone
lines and inside assignments or nested expressions.

### Marked operations and multiplication animations

All standard click-through SVG animations (including FSM, PDA, BDD and linear
algebra) show a circular restart arrow and **End - click to restart (0)** below
the final frame. The next click returns to the first frame. Static single-frame
outputs do not show this indicator.

The shared declaration `svgHeight=800;` sets the web SVG output box's initial
height to 800 pixels, for any diagram or animation type. Omit it or use
`svgHeight=0;` to keep the stylesheet default (30em on desktop, 25em on narrow
screens). Values must be nonnegative integers. Manual vertical resizing still
works on desktop; the preference is reapplied on
each render, including auto-draw, and is preserved in generated scripts and
animation links. It changes the web container, not the exported SVG dimensions.
After packaging, run the shared declaration checks with:

```powershell
$cp = 'target\classes;target\svg-height-tests;target\XWizard\WEB-INF\lib\*;.tools\apache-tomcat-11.0.24\lib\servlet-api.jar'
javac -cp $cp -d target\svg-height-tests tests\SvgHeightTest.java
java -cp $cp SvgHeightTest
```

Optionally put a marker immediately after a symbolic operator:
`C = A * @product B`. Markers do not change precedence, evaluation or the normal
formula output. Names are case-sensitive and unique within a scene. Bare names
use letters followed by letters, digits or underscores; quote other names,
for example `A * @"first product" B`. Within quoted names, escape `"` as `\"`
and `\` as `\\`. The operators `+`, `-`, `*`, unary `+`/`-`, `^T` and `^-1`
accept markers, including inside nested expressions and function arguments.
This applies to matrix/scalar scene expressions, not scalar-function definitions.
Only matrix-by-matrix multiplication currently supplies a worked trace;
matrix-by-vector products also qualify, while scalar scaling does not.

```text
linalg:
A = [1 2 3; 4 5 6]
B = [7 8; 9 10; 11 12]
C = A * @product B
--declarations--
operation=product;
step=4;
--declarations-end--
```

Selecting `operation` shows just that operation's teaching frame. `step=0`
starts with an empty output matrix; `step=-1` (the default) shows the completed
result. For an m-by-k times k-by-n multiplication, there are exactly **m*n*k**
steps, each one scalar multiplication added to the running sum. Entries are
visited row by row, then column by column, accumulating terms left to right.
An output entry stays visibly unfilled (`square`) until its dot product is
complete; computed zeros are real filled entries. Frames show the current
row, column, term and running sum. The active left-factor entry is circled in blue
and the active right-factor entry in red, with matching circles around those
factors in the calculation below. Once the current dot product is complete, its
output entry and the formula's result are circled in green; earlier completed entries
remain uncircled. Step zero has no active entries to circle.
Matrix columns reserve the maximum width of their entries, including circles and
unfilled-cell placeholders, and rows reserve the maximum circle height/depth.
These measurements use the complete operands and final result in every frame,
so matrix entries and brackets stay in place as the calculation progresses.
The teaching frame also has a fixed width (at least 14 cm, expanded for wider
matrix equations), so changing captions cannot recenter the matrices.
Zero-length dot products are filled with zero
at step zero. Missing markers, duplicate names, unsupported selected operations
and out-of-range steps report errors.

For declaration-safe names, `operation` uses URL encoding: e.g.
`operation=first+product;` for `@"first product"`, `%2B` for a literal plus,
`%23` for a hash and `%25` for a percent sign. The **Select multiplication**
and **Animate multiplication** conversions accept the original name without
`@` and perform this encoding automatically.

**Animate multiplication** creates the standard declaration-driven, click-through
SVG animation, including the initial empty frame and every arithmetic step.
The same sequence can be written manually:

```text
--declarations--
operation=product;
step=0;
prep0=#x0=this#;
@{prepA=#xA=x~{A-1}~.nextMultiplicationStep#;}@.for[A, 1, x0.multiplicationSteps]
animate=x0@{->xB}@.for[B, 1, x0.multiplicationSteps];
--declarations-end--
```

`this.multiplicationSteps` returns the number of arithmetic steps as plain text;
`this.selectOperation[product]`, `this.multiplicationStep[4]`,
`this.nextMultiplicationStep` and `this.animateMultiplication[product]` are
available in conversion chains. As with FSM/PDA/BDD animations, rendering requires
the configured LaTeX and PDF-to-SVG executables. Each frame is rendered separately,
so large products can produce expensive animations.

For Java consumers, `scene.getOperation(name)` exposes captured operands and
the result, unaffected by later variable redefinitions. Its `multiplication()`
provides `getStepCount()` and lazy `getStep(index)` snapshots. The returned state
contains a `Double[][]` incomplete matrix (`null` means unfilled), zero-based
active indices, the previous sum, scalar product and partial sum. No list of
all intermediate matrices is retained.

### Matrix functions

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
| `eigenvalues(A)` | Real eigenvalues of a square matrix, as a column vector in descending order, with algebraic multiplicities |
| `eigenvectors(A)` | Unit-length real eigenvectors as columns, grouped by descending eigenvalue |

```text
A = [4 1; 2 3]
values = eigenvalues(A)     // (5, 2)
vectors = eigenvectors(A)
Eigenvalues(A)
Eigenvectors(A)
evd(A)
```

These two functions work in assignments and expressions without displaying a
full decomposition. Capitalized forms explain Av = lambda*v, the algorithm,
ordering and ML applications. For diagonalizable matrices, column j of
`eigenvectors(A)` corresponds to entry j of `eigenvalues(A)` (and column j of
the EVD factor Q). Vector signs and bases for repeated eigenvalues are not unique.
Eigenvectors of a nonsymmetric matrix need not be orthogonal.
For defective matrices, `eigenvalues` still returns all algebraic multiplicities,
while `eigenvectors` returns only an independent basis for each eigenspace,
so it has fewer columns and no longer pairs entry-by-entry with the full
eigenvalue list. Neither function requires a successful diagonalization.
Both require an entirely real spectrum; non-real eigenvalues, numerical failures
and invalid shapes produce line-numbered errors rather than partial results.
`evd`/`Evd` name the display-only eigenvalue decomposition; `eig`/`Eig` remain
compatibility aliases for older scripts.

The web example dropdown uses descriptive English/German titles for the
linear-algebra examples, covering transformations, decompositions and calculus.

Capitalize a function's first letter to request an algorithm explanation before
its result: `Det(A)`, `Rank(A)`, `Trace(A)`, `Norm(A)`, `Rref(A)`, `Basis(A)`,
`Nullspace(A)`, `Inv(A)`, `Transpose(A)`, `Span(A)`, `Svd(A)`, `Evd(A)`,
`Eigenvalues(A)`, `Eigenvectors(A)`.
Capitalized calls show input-specific intermediate calculations, not just
algorithm descriptions. Elimination-based calls show actual row operations and
intermediate matrices. `Trace` accumulates diagonal entries, `Norm` accumulates
the length using stable hypot steps, `Transpose` moves each row to its output
column, and `Basis`/`Span` explicitly select the original pivot columns.
`Det` also shows the signed diagonal product (without a Laplace expansion).
`Evd`, `Eigenvalues` and `Eigenvectors` show the actual Jacobi rotations or QR
factorizations and 1-by-1/2-by-2 spectral block calculations. Eigenspace
construction shows shifted matrices, SVD snapshots, selected singular vectors
and matching eigenvalues. EVD additionally assembles/inverts the eigenvector
matrix and checks reconstruction; failed decompositions retain the steps reached
before the failure. For long eigen iterations, the first eight Jacobi sweeps
and every tenth sweep, or the first eight QR iterations and every hundredth,
are shown with explicit labels; the final spectral matrix is always included
when reached. Values are rounded for display, not during computation.
Capitalized differential operators show the expression-tree differentiation
rules, intermediate partial derivatives, component assembly and numeric point
substitution. Hessians include both differentiation passes; Laplacians and
divergence show accumulation, and curl shows the component differences.
Lowercase calls remain concise, with unchanged results and source expressions.
Every capitalized function also includes a short **ML use** summary, covering
typical machine-learning applications and relevant numerical caveats.
`Nullspace(A)` explains the kernel as the solutions of Ax=0, then shows
rank-nullity, free-variable parameters, solved pivot equations, each basis-vector
construction and the general solution x=Nt. It distinguishes an empty basis
for the trivial kernel from the zero vector, and explains why adding a kernel
vector leaves a linear model's predictions unchanged.
Numeric/matrix variants also work in assignments and nested expressions, e.g.
`d = Det(A) + Rank(A)`. `Span`, `Svd`, and `Evd` remain display-only.
`Svd(A)` shows the actual Jacobi rotations (column pairs, inner products and
rotation coefficients), intermediate working/rotation matrices after each
sweep, column lengths, and the sorted, normalized factors. Wide matrices are
processed through their transpose, with the factor mapping explained.
Both `svd` and `Svd` use the thin SVD: for an m-by-n matrix and k = min(m,n),
U is m-by-k, Sigma is k-by-k, and V is n-by-k; the displayed V* is V-transpose,
k-by-n, because all entries are real. The built-in example has 2 rows and 3
columns: V is 3-by-2 and V* is 2-by-3. A full SVD instead has square U and V
and an m-by-n Sigma; the additional directions multiply zeros.
`Svd` also explains the notation: V-transpose swaps rows and columns, whereas
V* is the conjugate transpose (also written V^H), which additionally replaces
each complex entry a+bi by a-bi. They agree for the real matrices supported here.
In A = U Sigma V*, V* takes inner products with the right singular vectors
(columns of V) to obtain input coordinates. Orthonormal columns give V*V = I;
for a rectangular thin V, V* is not a two-sided inverse.
Capitalization does not change the numeric result or rewrite the source.
`Det(A)` shows Gaussian elimination followed by the result, without the Laplace
expansion used by lowercase `det(A)`.

`span(A)` displays the subspace spanned by A's columns, using an independent
generating set. `span(u, v, ...)` accepts column vectors of equal dimension.
Like `svd(A)` and `evd(A)`, `span(...)` is display-only and must stand on its own
line. Use `basis(A)` when you want a matrix you can reuse in calculations.
The span of zero vectors is displayed as `{0}`. A trivial kernel is represented
by an n-by-0 basis matrix, not by a zero basis vector.

Rank, row reduction and basis calculations use partial pivoting with a relative
tolerance of `1e-12`; nearly dependent columns may therefore be treated as dependent.
Invalid dimensions, unknown functions and invalid arguments report a line-numbered
error. Legacy `matrix:` scripts with whitespace-separated entries remain accepted.

### Functions and differential operators

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

With `showPlot=true`, direct `grad`/`Grad` results also show their scalar function:
one input variable produces a local curve with the evaluation point and tangent;
two variables produce a contour map with labeled function levels, the point and
a unit-length gradient-direction arrow. Each local window extends two units
either side of the evaluation point in parameter order. These are sampled
approximations, not exact plots; undefined/non-finite samples are omitted with a
visible notice, and discontinuities may not be resolved. Aliases retain the
function view; transformed vectors do not. Three-variable gradients retain the
existing 3D arrows without a function surface (which would require slices or
level surfaces). The original vector plots and their true gradient magnitudes
are unchanged.

Function bodies support `+`, `-`, `*`, `/`, powers `^`, parentheses,
`sin`, `cos`, `exp`, and natural logarithm `log`. Powers are right-associative;
`-x^2` means `-(x^2)`. Only declared parameters and numeric constants can be used
in a body; functions cannot capture scene objects or call other user functions.
Vector-valued functions use a tuple, e.g. `F(x, y) = (x*y, sin(x))`.
Definitions must precede their use. Function names and parameter names are
case-sensitive; built-in function names are reserved. These definitions currently
serve differential-operator calls; direct function evaluation is not yet supported.

All of these operators take a function name and a column-vector evaluation point:

For a one-variable function, a scalar point is also accepted: `p = (2)` followed
by `grad(f, p)` works, as do `grad(f, 2)` and `grad(f, [2])`. Parentheses still
group scalar arithmetic; the operator interprets the scalar as a one-coordinate
point. Gradient results remain column vectors, and Jacobians/Hessians remain matrices.

| Operator | Result and convention |
| --- | --- |
| `grad(f, p)` | Gradient of a scalar function, an n-by-1 column |
| `jacobian(F, p)` | m-by-n matrix: entry (i,j) is the derivative of output i with respect to parameter j; also accepts a scalar function (one row) |
| `hessian(f, p)` | n-by-n matrix of second derivatives of a scalar function, in parameter order |
| `laplacian(f, p)` | Scalar sum of pure second derivatives, equal to `trace(hessian(f, p))` |
| `divergence(F, p)` | Scalar sum of derivatives of component i with respect to parameter i; requires n outputs for n inputs |
| `curl(F, p)` | Three-component curl of a 3D field; requires three inputs and three outputs |

`Jacobian`, `Hessian`, `Laplacian`, `Divergence`, and `Curl` additionally display
the definition of the operator and its symbolic derivative expression before
substituting the point. The lowercase forms remain concise. Matrix, vector and
scalar results can all be reused in expressions; Jacobians and Hessians remain
matrices even for a one-variable function. For a twice continuously
differentiable scalar function, the Hessian is symmetric.

```text
f(x, y) = x^2 + 2*x*y + 3*y^2
p = (1, 2)
Hessian(f, p)       // [[2, 2], [2, 6]]
laplacian(f, p)     // 8
F(x, y) = (x*y, sin(x))
Jacobian(F, p)     // [[2, 1], [cos(1), 0]]
divergence(F, p)    // 2
G(x, y, z) = (-y, x, 0)
Curl(G, (1, 2, 3))  // (0, 0, 2)
```

The point must have one component per parameter; for one variable use a 1-by-1
column matrix such as `grad(f, [2])`. Invalid domains, non-finite results, and
dimension mismatches give line-numbered errors. Derivatives are real-valued
where the differentiation rules apply; non-finite derivative values are rejected.

With `showPlot=true`, an assigned gradient such as `g = grad(f, p)` is drawn
from `p` to `p + g` in 2D/3D. It remains an ordinary column vector in calculations.
Aliases of `g` retain the anchor; newly computed vectors such as `A * g` are
drawn from the origin. Standalone gradient calls display formulas but are not
added to the plot. Contours and gradient fields are not implemented yet.
Curl vectors are also anchored at their evaluation point; Jacobians and Hessians
use the existing matrix-plot rules rather than anchored vector arrows.

### Statistics in the same script

Statistics use the existing `linalg:` type in the ML tab, so their scalar results
can be combined with matrix operations:

```text
linalg:
data = [2 4 4 4 5 5 7 9]
mu = mean(data)
v = variance(data)
sigma = std(data)
Mean(data)
Variance(data)
Std(data)
A = [1 0; 0 2]
scaled = sigma * A
--declarations--
showPlot=true
--declarations-end--
```

Here `mu=5`, `v=4` and `sigma=2`. Each finite entry of a **nonempty row or
column vector** is one equally weighted observation. Use `[2]` for a single
observation; scalars, empty vectors and general matrices are rejected rather than
guessing an aggregation axis. Transposing a data vector does not change its
statistics. Nested expressions such as `mean(eigenvalues(A))` work too.

- `mean(data)`: arithmetic mean, `sum(x_i)/n`.
- `variance(data)`: **population** variance, `sum((x_i-mu)^2)/n`, not the
  sample estimator with denominator `n-1`.
- `std(data)`: square root of population variance, in the original data units.

Singletons and constant data have zero spread. Capitalized calls show actual
running sums, deviations, squared deviations and accumulated totals, as
appropriate, plus a short ML-use summary. Numeric results and traces share
two-pass high-precision decimal calculations (DECIMAL128 division and products).
This avoids premature overflow and cancellation: `std` can be representable even
when `variance` would overflow or underflow. An unrepresentable requested scalar
is an explicit line-numbered error, not infinity or silent zero.

With `showPlot=true`, every statistics call also contributes an **empirical
relative-frequency histogram**, even when assigned or nested in another
expression. Identical datasets share one plot; later data redefinitions preserve
earlier snapshots. There are `ceil(sqrt(n))` equal-width bins, capped at 12; constant
data use one bin. Each bin includes its left edge, and only the final bin includes
its right edge. Bars show observation fractions, and labels show counts: **bin
masses**, not bar areas, sum to one. Red marks the mean; dashed lines and shading
mark `mu +/- sigma`. This is not an assumed Gaussian density, a confidence
interval, or a claim of any fixed probability coverage. No theoretical
distribution functions are introduced in this first batch.

### Precision, recall and Bayes' formula

These functions also return reusable scalars in `linalg:`:

| Function | Formula | Meaning |
| --- | --- | --- |
| `precision(tp, fp)` | `TP / (TP + FP)` | Of predicted positives, how many are correct? |
| `recall(tp, fn)` | `TP / (TP + FN)` | Of actual positives, how many are found? (Sensitivity.) |
| `bayes(prior, likelihood, evidence)` | `P(H) * P(E|H) / P(E)` | Posterior probability `P(H|E)` after observing evidence `E`. |

TP, FP and FN are true positives, false positives and false negatives. Counts
must be finite nonnegative scalars; fractional counts support weighted
observations. A zero denominator is **undefined** and produces a line-numbered
error rather than silently returning zero or one.

Bayes arguments must be probabilities in `[0,1]`, with positive evidence `P(E)`.
They must also be consistent:
`prior * likelihood <= evidence <= prior * likelihood + (1 - prior)`.
Evidence is supplied explicitly; it is **not** the false-positive rate.

```text
linalg:
tp = 80
fp = 20
fn = 10
p = precision(tp, fp)
r = recall(tp, fn)
Precision(tp, fp)
Recall(tp, fn)
// H: condition present; E: positive test
prior = 0.01
likelihood = 0.9
evidence = likelihood * prior + 0.05 * (1 - prior)
posterior = bayes(prior, likelihood, evidence)
Bayes(prior, likelihood, evidence)
```

This gives precision `0.8`, recall about `0.8889` and posterior about `0.1538`.
The diagnostic test's `0.9` sensitivity is not its posterior: the base rate and
false-positive rate matter. Capitalized calls show the definition, captured
inputs, denominator or joint-probability calculation, substituted ratio and ML
uses. They work in assignments and nested expressions such as
`precision(80,20) * [1 0;0 1]`. They do not create observation histograms.
Decimal intermediates avoid premature overflow/underflow; an unrepresentable
nonzero scalar result is an explicit error.

### Binomial and Poisson distributions

`binomial(n, p, k)` returns the probability of **exactly k successes** in n
independent trials with a constant success probability p:
`P(X=k) = choose(n,k) * p^k * (1-p)^(n-k)`.

`poisson(lambda, k)` returns the probability of **exactly k events** when the
expected count in a fixed exposure interval is lambda:
`P(X=k) = exp(-lambda) * lambda^k / k!`.
For a homogeneous Poisson process, lambda is rate times exposure.

```text
linalg:
b = binomial(10, 0.3, 3)
Binomial(10, 0.3, 3)
q = poisson(4, 2)
Poisson(4, 2)
--declarations--
showPlot=true;
--declarations-end--
```

The results are approximately `0.2668` and `0.1465`. These are reusable scalar
**PMFs**, not cumulative probabilities, generated samples or distribution vectors.
Capitalized calls explain assumptions, substitute inputs into the formula, show
stable log-PMF calculation steps, exponentiate the result and give mean/variance
and ML uses. The implementation avoids factorial overflow and cancellation near
the mode using Stirling corrections and a deviance series.

Arguments must be finite scalars. n and k are nonnegative integers up to
`9007199254740991`; p must lie in `[0,1]`, lambda in `[0,9007199254740991]`.
Binomial k>n returns zero; negative or fractional counts are rejected.
Degenerate cases n=0, p=0/1 and lambda=0 are handled explicitly. A requested
nonzero probability that underflows double precision produces an explicit error.

With `showPlot=true`, calls also create discrete probability-mass plots and
highlight k in red, including when assigned or nested. Identical calls share one
plot; input redefinitions preserve earlier distributions. Binomial n<=100 shows
all possible outcomes. Larger binomials and Poisson show a bounded window near
the mean: up to six standard deviations plus six outcomes per side, capped at
50 per side (**101 consecutive outcomes**). Plots label truncated ranges,
estimated mass outside the window and requested outcomes outside it.
They **do not renormalize** the displayed masses. Numerically underflowed plotted
tail masses are explicitly reported as drawn zeros. These plots are theoretical
PMFs, unlike the empirical histograms produced by `mean`, `variance` and `std`.

### Neural networks: explicit forward passes and backpropagation

ANN operations stay in `linalg:` and return ordinary reusable matrices or scalars.
They do not create opaque network objects or an automatic differentiation tape.
Use saved forward inputs and preactivations to apply the chain rule explicitly.
Capitalized calls show the same result plus actual per-entry numerical steps,
shape/derivative conventions and ML uses.

#### Beginner explanations and activation plots

Capitalized ANN calls first introduce the operation in everyday terms, then
give the formal rule and concrete calculation. They explain weighted input
contributions and bias, preactivation versus activation, predictions versus
targets, local slopes, upstream loss gradients, and the difference between
backpropagation and the subsequent learning-rate-controlled parameter update.
The loss examples and chain-rule notation connect these ideas to the numbers.

Enable `showPlot=true;` for:

- **ReLU, sigmoid and tanh:** side-by-side activation and local-derivative curves
  over the fixed input window `[-5,5]`. Red dots mark up to 12 distinct supplied
  preactivations in that window; captions report off-window and capped markers.
  Each panel has its own labeled vertical scale. The ReLU derivative has a jump,
  not a connecting diagonal; at zero, a filled dot shows the implementation's
  chosen derivative zero, while an open dot excludes one.
- **Softmax:** class-probability bars for each of the first four example columns,
  showing at most the first 12 classes. Captions report class/example limits and
  omitted class probability mass. Shares are computed across **all** classes
  before plotting and are not renormalized for the visible subset.
  Softmax couples its inputs, so it is deliberately not shown as an independent
  scalar curve or scalar derivative.

Plots work for lowercase/capitalized, standalone/assigned/nested activation calls
and their backward forms. Fused cross-entropy calls contribute the softmax view
of their logits. Input snapshots survive redefinitions and saved scenes.
Scalar-activation views share one curve pair per activation; repeated identical
softmax inputs share a view. Curve derivatives are **local slopes**, not the
upstream-multiplied backward result, weight updates or training histories.
Existing geometric vector/matrix views remain available alongside these views.
Plots remain opt-in, independent of capitalization; `showFormulas=false;` may
hide formulas while retaining plots.

The dropdown example **ANN basics: activation curves, slopes and class shares**
introduces these visualizations without requiring a complete training example:

```text
linalg:
z = [-5 -2 -1 0 1 2 5]
Relu(z)
Sigmoid(z)
Tanh(z)
Softmax((2, 1, 0, -1))
--declarations--
showPlot=true;
--declarations-end--
```

**Shape convention:** columns are independent examples; rows are features,
neurons or classes. For a layer with `i` inputs, `o` outputs and batch size `B`,
`W` is `o x i`, `X` is `i x B`, `b` is `o x 1`, and `Z` is `o x B`.
One example is a column vector, including `[x]` for one feature. A row vector in
softmax means **one class with multiple examples**, not multiple classes.
Only `dense` broadcasts a bias column: ordinary `+` still requires equal shapes.

| Function | Meaning |
| --- | --- |
| `dense(W, X, b)` | `Z = W*X + b`, broadcasting the bias to each column |
| `relu(Z)`, `sigmoid(Z)`, `tanh(Z)` | Elementwise activations, preserving shape; scalars also accepted |
| `softmax(Z)` | Stable class probabilities, normalized separately in each column |
| `hadamard(A, B)` | Elementwise product, with exactly matching shapes |
| `mse(A, T)` | Regression loss: mean squared difference over **all entries**, no factor of 1/2 |
| `crossentropy(Z, T)` | Stable **softmax + cross-entropy from logits**, averaged over example columns |
| `msegrad(A, T)` | Loss gradient with respect to predictions: `2*(A-T)/N` |
| `crossentropygrad(Z, T)` | Fused loss gradient with respect to logits: `(softmax(Z)-T)/B` |
| `reluback(Z, GA)`, `sigmoidback(Z, GA)`, `tanhback(Z, GA)` | Preactivation and upstream gradient in; preactivation gradient out |
| `softmaxback(Z, GA)` | `GZ_i = A_i*(GA_i - sum_j A_j*GA_j)`, separately per example |
| `densegradw(GZ, X)` | Weight gradient `GZ * X^T` |
| `densegradb(GZ)` | Bias gradient: sum over example columns |
| `densegradx(W, GZ)` | Input gradient `W^T * GZ` |

Backward activation calls take **preactivations**, not activated values. Their
upstream gradient argument is the derivative of the final scalar loss with
respect to the activation's output. They return the derivative with respect to
its input. ReLU chooses derivative zero at zero. Softmax's backward operation
includes the full coupling between classes, rather than an elementwise mask.

Loss gradients already include their loss's averaging factor. Dense backward
operations **sum**, not average again. For `crossentropy`, targets must have the
same shape as logits, with entries in `[0,1]` and each column summing to one
(tolerance `1e-12`); one-hot and soft-label columns are accepted. The implementation
retains the actual target column sum in its gradient, so even rounding deviations
within that tolerance differentiate the exact supplied objective.
Do **not** pass probabilities to `crossentropy` or apply `softmaxback` again
after `crossentropygrad`: the fused operation already differentiates softmax.

This is a complete one-neuron forward/backward calculation:

```text
linalg:
W = [0.2 0.3]
x = (1, 2)
b = [0.1]
target = [1]
z = Dense(W, x, b)
a = Sigmoid(z)
loss = Mse(a, target)
ga = Msegrad(a, target)
gz = Sigmoidback(z, ga)
gW = Densegradw(gz, x)
gb = Densegradb(gz)
gx = Densegradx(W, gz)
eta = 0.1
Wnew = W - eta*gW
bnew = b - eta*gb
newloss = mse(sigmoid(dense(Wnew, x, bnew)), target)
```

For multiple layers, pass `densegradx(W_next, GZ_next)` into the preceding
activation's backward call, and continue toward the first layer. **Calculate all
gradients with the original weights before updating any parameter.** Two named
dropdown examples demonstrate a two-layer network and batched softmax
classification, including one SGD step that decreases the example's loss.

Sigmoid uses sign-dependent evaluation, softmax subtracts the column maximum,
and cross-entropy uses log-sum-exp (so confidently wrong predictions can produce
finite losses without `log(0)`). Backward sigmoid/tanh use stable derivative
formulas even when their forward values round to one. Extremely small activations
or derivatives can still underflow in ordinary double precision. Empty inputs,
nonfinite entries, incompatible shapes and overflowing intermediates cause
line-numbered errors; general implicit broadcasting is deliberately not added.

**Extension boundary:** dense, activation and loss operations all consume values
and explicit upstream gradients, with serializable worked-step snapshots.
Future 2D convolution/pooling operations can follow the same forward/backward
contract using matrices for single-channel images. Multichannel kernels will need
an explicit channel/tensor layout; it is not silently inferred now. Convolution,
pooling, automatic differentiation, optimizers beyond explicit SGD updates, and
training loops are **not implemented** in this milestone. No random initialization
or hidden mutable network state is required for hand-worked examples.

ANN regression checks (after compiling the application):

```powershell
$cp = 'target\classes;target\XWizard\WEB-INF\lib\*'
javac -cp $cp -d target\linalg-tests tests\NeuralNetworkTest.java
java -cp "target\linalg-tests;$cp" NeuralNetworkTest
```

These compare backward primitives and complete batched two-layer networks
against central finite differences, including weights, biases and inputs.

### Multilayer perceptron training animations

Linear-algebra conversion buttons are context-sensitive. MLP controls appear
only with an MLP configuration; marked-multiplication controls require a
matrix/matrix `*` marker, and stepping controls require a selected operation.
Next-step buttons are hidden at the final step. Teaching views hide ordinary
matrix-editing and plot controls that would not affect the displayed animation.
Plot/formula toggles appear only when they can change the current display.
Methods remain available in script chains; only their buttons are filtered.
The general Multiply and Change basis buttons in the ordinary view accept
arbitrary expressions, not just existing matrix names.

Run the visibility and generated web-button checks after packaging:

```powershell
$cp = 'target\classes;target\button-tests;target\XWizard\WEB-INF\lib\*;.tools\apache-tomcat-11.0.24\lib\servlet-api.jar'
javac -cp $cp -d target\button-tests tests\LinAlgConversionButtonsTest.java
java -cp $cp LinAlgConversionButtonsTest
```

An MLP animation uses the existing dense/activation/loss primitives to show one
complete training step, **one neuron at a time**, with both a network diagram and
matrices. The topology may contain any number of dense layers, with different
neuron counts. The network stays visible while the calculation panel shows only
the active layer's changing values and the inputs needed to explain them.
Each frame names the current layer/neuron and describes its calculation.
Every network edge displays its weight (destination neuron = matrix row,
source neuron = column). Labels use the current parameters, changing row by
row during SGD and retaining the updated weights in the second forward pass.
Supply one input example as a column vector and its target as a scalar or vector:

```text
linalg:
x = (1, 2)
target = 0.8
W1 = [0.1 0.2; -0.3 0.4]
b1 = (0, 0)
W2 = [0.5 -0.2; 0.3 0.1]
b2 = (0.1, -0.1)
W3 = [0.7 -0.4]
b3 = [0.05]
--declarations--
mlpLayers=#W1,b1,tanh;W2,b2,relu;W3,b3,sigmoid#;
mlpInput=x;
mlpTarget=target;
mlpLoss=mse;
mlpLearningRate=0.1;
mlpStep=0;
svgHeight=800;
--declarations-end--
```

The `#...#` literal around `mlpLayers` is required because layers are separated
by semicolons. Each layer specifies the **names** of its weight matrix and bias
column, followed by `identity`, `relu`, `sigmoid`, `tanh`, or `softmax`. Weight
rows are neurons; columns are inputs. Bias has one entry per output neuron.
All parameter names must be distinct; tied weights are not supported.
The selected scene's latest definitions supply these values.

MLP targets accept `target=0.8` (one output) or `target=[0.3,0.7]` (two outputs).
Row vectors `[0.3 0.7]`, column matrices `[0.3;0.7]`, and column vectors
`(0.3,0.7)` also work. The animation normalizes targets to columns internally
without changing the original scene or general matrix syntax. The target entry
count must match the output-neuron count; multi-row, multi-column targets are
rejected because the animation handles one example. Input and bias shapes
remain column vectors. In ordinary expressions, `[a,b]` is still a row matrix,
`(a,b)` a column vector, and `(a)` a grouped scalar.

`mlpLoss=mse` is the default. Classification uses `mlpLoss=crossentropy` with
`softmax` as the final activation and a probability target column summing to one.
The loss and output gradient then use the stable fused logits implementation;
softmax is not differentiated a second time. Softmax is allowed only in the
output layer. It depends on all output logits, which are computed together,
while the animation reveals one probability at a time. MSE can also use a
softmax output; its backward step includes the full softmax Jacobian coupling.

For N neurons across all non-input layers there are **4*N+1** steps:
N forward neurons (weighted sum plus activation), one loss step, N backward
neurons in reverse layer order (delta plus weight/bias gradient row), and N
parameter-row updates, then N forward neurons using the updated parameters.
Step zero is the initial state. `mlpStep=-1`, the default,
shows the final frame. Blue highlights forward neurons, red highlights backward
neurons, and green highlights updated rows. Unvisited activations and gradients
are blank. All gradients are calculated with the **original weights** before
any parameter changes; the update is one simultaneous SGD step, displayed row
by row. Saved activations/gradients stay visible during the update. A second
forward pass then recomputes each neuron's weighted sum and activation, showing
the old activation vector alongside the new one. Visited network nodes switch
to their new values; unvisited nodes retain the old values until recomputed.
This is not a second training update. One additional completion frame clears all
calculation circles and active-neuron/edge highlights while retaining the updated
network values. The final frame reports the updated
prediction, target, and loss before and after the update. For the five-neuron
example this produces 22 steps plus the initial frame (23 frames).
A positive learning rate does **not** guarantee that loss decreases.
The loss frame compares the prediction with the target and defines the selected
loss: mean squared error averages squared output errors (with a numerical
substitution), while cross-entropy measures disagreement with target
probabilities. Backward frames explain gradients as sensitivities of that loss;
update frames compare original and current parameters.

Use **Animate MLP training** to generate the standard click-through SVG.
The conversion methods `this.mlpStep[4]`, `this.nextMlpStep`,
`this.mlpSteps` (plain-text step count), and `this.animateMlp` also work in
declaration/preprocessor chains. The equivalent loop is:

```text
prep0=#x0=this#;
@{prepA=#xA=x~{A-1}~.nextMlpStep#;}@.for[A,1,x0.mlpSteps]
animate=x0@{->xB}@.for[B,1,x0.mlpSteps];
```

Keep this loop alongside the MLP declarations and `mlpStep=0`. MLP selection and
marked matrix-multiplication selection are mutually exclusive. This milestone
animates one example and one training step, not mini-batches or repeated epochs.
Invalid shapes, names, activations, losses, step indices, nonfinite inputs and
nonpositive/nonfinite learning rates report errors. Large networks produce
large diagrams and expensive per-frame rendering.
The web LaTeX source-size cap is 1,000,000 characters, allowing the generated
TikZ and fixed-cell layout code in MLP frames. This cap applies to both prepared
LaTeX source and converted LaTeX scripts; Graphviz limits and execution timeouts
are unchanged.

The network topology and matrix cell dimensions (including original/updated
parameter values and highlight circles) are reserved across frames. The lower
calculation panel may change size as the active layer or phase changes.
`LinAlg.getMlpTrace()` exposes layer outputs, deltas, parameter
gradients, updated parameters, losses, step count and phase/neuron indices.
Rendering or generating animation source does not change the matrices in the
original script.

Run its finite-difference and animation-loop checks after compiling:

```powershell
$cp = 'target\classes;target\mlp-tests;target\XWizard\WEB-INF\lib\*;.tools\apache-tomcat-11.0.24\lib\servlet-api.jar'
javac -cp $cp -d target\mlp-tests tests\MlpAnimationTest.java
java -cp $cp MlpAnimationTest
# Optional full SVG rendering checks with the existing renderer executables:
java '-Dmlp.svgDirectory=target\mlp-svg' `
     '-Dmlp.pdflatex=C:\path\to\pdflatex.exe' `
     '-Dmlp.pdf2svg=C:\path\to\pdf2svg.exe' -cp $cp MlpAnimationTest
```

### Function regression checks

After compiling and packaging the application (`mvn package`), these
dependency-free checks can be run from the repository root in PowerShell:

```powershell
New-Item -ItemType Directory -Force target\linalg-tests | Out-Null
javac -cp 'target\classes;target\XWizard\WEB-INF\lib\*' -d target\linalg-tests tests\LinAlgFunctionsTest.java
$servlet = '.tools\apache-tomcat-11.0.24\lib\servlet-api.jar' # Or your container's Servlet 6.1 API jar.
java -cp "target\classes;target\linalg-tests;target\XWizard\WEB-INF\lib\*;$servlet" LinAlgFunctionsTest
```

These checks include operation-marker parsing, trace states and declaration-loop
expansion. To also render and check a 13-frame multiplication SVG, supply the
existing renderer paths and an output directory:

```powershell
java '-Dlinalg.markerSvgDirectory=target\multiplication-svg' `
     '-Dlinalg.pdflatex=C:\path\to\pdflatex.exe' `
     '-Dlinalg.pdf2svg=C:\path\to\pdf2svg.exe' `
     -cp "target\classes;target\linalg-tests;target\XWizard\WEB-INF\lib\*;$servlet" LinAlgFunctionsTest
```
