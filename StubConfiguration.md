#Stub Configuration
Stub configuration works well for usual stubs as we meet them in the evoleq library.

The configuration-process for FxComponents is more involved. 
This is the case for:
* FxStageComponent
* FxSceneComponent
* Something : FxParentComponent

The following example demonstrates the difficulties:
```kotlin
// fxParent - some  FxParentComponent configuration function
// fxChild - some FxComponent configuration function

fxParent{
    view{ /* ... */}
    child(fxChild{
        view{/* ... */}
        stub child@{
            //  stub def :
            //  This stub shall be a child of this@parent
        }
    })
    stub parent@{

        // do something concerning child@ 
        
        // define other children
        child(stub{
            /* stub def */
        })
    }
    fxRuntime{
        // do something fx-related when the component is shown: e.g. 
        // 1. add children
        // 2. alter style 
        // 3. alter displayed data
    }
}
```

## Configuration of FxComponents
### Id Generation
## Idea: 

## Idea: Manage the config process using evoleq-flows

The tree-like Stub-structure is generic. It's configuration can be extended all the time.

Phases: (unordered listing!)
* PreConfigurationPhase:
    * call methods:
        - id
        - stub
        - child
        - view
        - @FxBorderPaneConf : top, left, center, right, bottom
        - ...
* ConfigurationPhase: 
    * StubExtensionPhase: 
    * AddFxChildrenPhase: Add children 
    * StubConfigurationPhase: 
    * ViewConfigurationPhase:
* ComponentReadyPhase
* RunTimePhase: 

```kotlin
    sealed class ConfigurationPhase{
        class PreConfiguration : ConfigurationPhase()
        sealed class Configuration : ConfigurationPhase() {
            class StubExtension : Configuration()
            class AddChildren : Configuration()
            class StubConfiguration : Configuration()
            class ViewConfiguration : Configuration()
        }
        object ComponentReady : ConfigurationPhase()
        object RunTime : ConfigurationPhase()
    }
```




States:
* stub initialized
* id set
* view ready
* children ready
* stub ready


##Reconfiguration functions
Problem: loss of information, since stubs are completely regenerated
```kotlin

fun <D> Stub<D>.reconfigure(configuration: StubConfiguration<D>.()->Unit): Stub<D>  {
    // create new stub-config
    val newConfig = StubConfiguration<D>()
    // copy data of stub to newConfig
    newConfig.id = this.id
    newConfig.evole = this.evolve
    newConfig.stubs = this.stubs
    // configure and return 
    return newConfig.configure()
}

```

```kotlin
fun <N: Node, D> FxNodeComponent<N,D>.reconfigure(configuration: FxNodeComponentConfiguration<N, D>.()->Unit): FxNodeComponent<N,D> 
{}

```


