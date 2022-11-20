# How to get test coverage in VSCode

Install extension: [Coverage Gutters](https://marketplace.visualstudio.com/items?itemName=ryanluker.vscode-coverage-gutters)

Run the following command to generate the coverage report:

```
mvn jacoco:prepare-agent test install jacoco:report
```

On the status bar, click on `Watch`.

Open the class and see the test coverage details.