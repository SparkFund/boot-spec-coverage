# boot-spec-coverage

Check if your `clojure.spec` specs are actually exercised in your
test suite.

## Usage

In boot, add the following to your build file.

```
(require '[spec-coverage.boot :refer :all])`
```

Then `boot spec-coverage` will trigger the coverage checker.

## License

Copyright © 2017 SparkFund

Distributed under the Apache License, Version 2.0.
