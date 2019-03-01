##Versions

1.0.2: AppManager, FxComponents:
* AppManager: <br>
Use JavaFX-Application class to
    * do stage-management 
    * launch and manage applications  

  Note that AppManager implements Configuration<Stub<D>>
  making it easy to use it in the evoleq context
  
* FxComponents: <br>FxComponents are Stubs equipped with a show-method
FxComponents shall be created using their dsls. By now the following FxComponents have been implemented 
    * FxStageComponent
    * FxSceneComponent
    * FxPaneComponent
    * FxBorderPaneComponent
    * FxGroupComponent
    * FxNodeComponent
    
   

1.0.1: AsyncFx: <br> Implementation of Evolving that first launches a coroutine and then runs a piece of code on the Fx Application Thread 

1.0.0: ParallelFx: <br> Implementation of Evolving that first launches a coroutine and then runs a piece of code on the Fx Application Thread and in coroutineScope
