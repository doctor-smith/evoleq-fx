package org.drx.evoleq.fx.dsl

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.stage.Stage
import org.drx.evoleq.dsl.Configuration
import org.drx.evoleq.evolving.Parallel
import org.drx.evoleq.fx.component.FxComponent
import org.drx.evoleq.fx.exception.FxConfigurationException
import org.drx.evoleq.fx.flow.fxComponentFlow
import org.drx.evoleq.fx.phase.FxComponentPhase
import org.drx.evoleq.fx.runtime.FxRunTime
import org.drx.evoleq.fx.stub.NoStub
import org.drx.evoleq.stub.Stub
import java.lang.Thread.sleep
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

typealias ID = KClass<*>



abstract class FxComponentConfiguration<N, D> :  Configuration<FxComponent<N, D>> {
    //
    lateinit var idConfiguration: KClass<*>
    lateinit var stubConfiguration: Stub<D>
    lateinit var viewConfiguration: ()->N

    var finish: Boolean = false
    var cancel: Boolean = false
    var component: FxComponent<N, D>? = null
    var fxRunTime: FxRunTime<N, D>? = null
    var fxRunTimeView: N? = null

    val launcher = Launcher<N,D>()

    class Launcher<N,D> {
        var id: ID? = null
        var stub: Stub<D>? = null
        var view: (()->N)? = null
        val fxChildren = ArrayList<Parallel<FxComponent<*,*>>>()
        val fxSpecials = ArrayList<Parallel<FxComponent<*,*>>>()
        val stubActions = ArrayList<Parallel<Stub<D>.()->Unit>>()

        fun launch(configuration: FxComponentConfiguration<N,D>): FxComponentPhase.Launch<N,D> = FxComponentPhase.Launch(
                configuration = Parallel{configuration},
                stub = Parallel{
                    while( stub == null ) {
                        kotlinx.coroutines.delay(1)
                    }
                    stub!!
                },
                view = Parallel{
                    while( view == null ) {
                        kotlinx.coroutines.delay(1)
                    }
                    view!!
                },
                id = Parallel{
                    while( id == null  ) {
                        kotlinx.coroutines.delay(1)
                    }
                    id!!
                },
                fxChildren = fxChildren,
                fxSpecials = fxSpecials,
                stubActions = stubActions

        )
    }

    override fun configure(): FxComponent<N, D> {
        Parallel<Unit> {
            val l = launcher.launch(this@FxComponentConfiguration)
            val terminate = fxComponentFlow<N, D>().evolve(l).get()
            assert(terminate is FxComponentPhase.Terminate)
        }
        while (!(finish || cancel) ) {
            sleep(1)
        }
        if(cancel) {
            throw Exception()
        }
        val comp = object : FxComponent<N, D> {

            override val id: ID
                get() = idConfiguration
            override val stubs: HashMap<ID, Stub<*>>
                get() = stubConfiguration.stubs

            override fun show(): N  {
                fxRunTimeView = viewConfiguration()
                return fxRunTimeView!!
            }

        }
        component = comp
        return comp
    }

    /******************************************************************************************************************
     *
     * Configuration functions
     *
     ******************************************************************************************************************/

    fun Any?.id(id: ID) {
        launcher.id = id
    }
    inline fun <reified Id> Any?.id() {
        launcher.id = Id::class
    }

    fun Any?.stub(stub: Stub<D>) {
        launcher.stub = stub
    }
    fun Any?.noStub() {
        launcher.stub = NoStub()
    }
    fun Any?.view(view: ()->N) {
        launcher.view = view
    }
    fun <M,E> Any?.child(child: FxComponent<M,E>) {
        try {
            launcher.fxChildren.add( Parallel { child } )
        } catch(exception: Exception) {
            throw FxConfigurationException.IdOfChildNotSet()
        }
    }
    fun <M,E> Any?.fxSpecial(child: FxComponent<M,E>) {
        launcher.fxSpecials.add( Parallel{child} )
    }
    fun Any?.stubAction(action: Stub<D>.()->Unit) {
        launcher.stubActions.add(Parallel{action})
    }
    fun Any?.fxRunTime(action: N.()->Unit) = Parallel<Unit>{
        while(fxRunTime == null) {
            kotlinx.coroutines.delay(1)
        }
        fxRunTime!!.fxRunTime(action)
    }
    fun Any?.shutdown() = Parallel<Unit> {
        while(fxRunTime == null) {
            kotlinx.coroutines.delay(1)
        }
        fxRunTime!!.shutdown()
    }
}

fun<N,D> Any?.fxComponent(configuration: FxComponentConfiguration<N,D>.()->Unit): FxComponent<N,D> {
    val conf = (object : FxComponentConfiguration<N, D>(){})
    conf.configuration()
    return conf.configure()
}

/**
 * Generic
 */
inline fun <reified N : Any, D> FxComponentConfiguration<N, D>.configure(noinline configure: N.()->Unit): N {
    val n = N::class.createInstance()
    n.configure()
    return n
}

/**
 * Generic
 */
fun<V> V.isFxParent(): Boolean = when(this) {
    is Group,
    is Pane -> true

    else -> false
}
/**
 * Stage
 */
fun <D> FxComponentConfiguration<Stage, D>.scene(component: FxComponent<Scene, D>): FxComponentConfiguration<Stage, D> {
    child(component)
    fxRunTime { scene = component.show() }
    return this
}

/**
 * Scene
 */
fun <P: Parent, D> FxComponentConfiguration<Scene, D>.root(component: FxComponent<P, D>, inject:(P)->Scene = { p -> Scene(p)}) : FxComponentConfiguration<Scene, D> {
    child(component)
    val root = component.show()
    val scene = inject(root)
    view{scene}
    return this
}

/**
 * AnchorPane
 */
fun Node.leftAnchor(anchor: Number) {
    AnchorPane.setLeftAnchor(this, anchor.toDouble())
}
fun Node.rightAnchor(anchor: Number) {
    AnchorPane.setRightAnchor(this, anchor.toDouble())
}
fun Node.topAnchor(anchor: Number) {
    AnchorPane.setTopAnchor(this, anchor.toDouble())
}
fun Node.bottomAnchor(anchor: Number) {
    AnchorPane.setBottomAnchor(this, anchor.toDouble())
}

/**
 * BorderPane
 */
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.top(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        top =  component.show()
    }
}
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.bottom(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        bottom =  component.show()
    }
}
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.left(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        left =  component.show()
    }
}
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.right(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        right =  component.show()
    }
}
fun <C : Node, D> FxComponentConfiguration<BorderPane, D>.center(component: FxComponent<C, D>)  {
    fxSpecial( component )
    fxRunTime {
        center =  component.show()
    }
}

/*
inline fun <reified N : Node, D> FxComponentConfiguration<N, D>.style(css: N.()->String): N {
    val n = N::class.objectInstance!!
    n.style = n.css()
    return n
}
*/


/**********************************************************************************************************************
 *
 * FxKeys
 *
 **********************************************************************************************************************/
class BottomId
class TopId
class RightId
class CenterId
class LeftId

