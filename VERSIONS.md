##Versions

1.1.0: Component-flow and Stable configuration-process
* Former dsl will still work. Related classes moved to ./deprecated and will be removed in version 1.2

1.0.2: AppManager, FxComponents
* AppManager: <br>Use JavaFX-Application class to
   * do stage-management 
   * launch and manage applications  

  Note that AppManager implements Configuration<Stub<D>>
  making it easy to use it in the evoleq context
  
* FxComponents: <br>FxComponents are Stubs equipped with a show-method
FxComponents shall be created using their dsls. By now the following FxComponents have been implemented 
    * [x] FxStageComponent
    * [x] FxSceneComponent
    * [x] FxPaneComponent
    * [x] FxBorderPaneComponent
    * [x] FxAnchorPaneComponent
    * [x] FxGroupComponent
    * [x] FxNodeComponent
        
   

1.0.1: AsyncFx: <br> Implementation of Evolving that first launches a coroutine and then runs a piece of code on the Fx Application Thread 

1.0.0: ParallelFx: <br> Implementation of Evolving that first launches a coroutine and then runs a piece of code on the Fx Application Thread and in coroutineScope
