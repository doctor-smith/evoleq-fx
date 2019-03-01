#EvoleqFX
Combine evoleq with JavaFX. 

##Goals
1. Provide JavaFX - specific  implementations of the Evolving interface
    * ParallelFx
    * AsyncFx
2. Provide Dsl to setup composable FxComponents (Application, Stages, Scenes, Nodes, ...) and
their functional components (Stubs, Flows, Gaps) in order to run them in some evoleq process. 
To make things clear, here is the base interface for all FxComponents 
```kotlin
interface FxComponent<N, D> : Stub<D> {
    fun show(): N
}

```


## Examples 
You can take a look at the component tests to see how the component-dsls and the 
app-manager work together.

Or you can take a look at the sources of [evoleq-examples](https://bitbucket.org/dr-smith/evoleq-examples/src/master/) 

## Versions
Take a look at the [version history](VERSIONS.md).



## Testing Hints

Use TestFX to launch an app-manager-instance before running the tests
```kotlin
    @Before
    fun launchBgAppManager() = runBlocking {
        FxToolkit.registerPrimaryStage()
        val manager = FxToolkit.setupApplication { BgAppManager() }
    }
```

Using evoleq we are dealing with async code all the time - Run your tests in a runBlocking-block
```kotlin
@Test fun testFunction() = RunBlocking{
    /* test implementation */
}
``` 