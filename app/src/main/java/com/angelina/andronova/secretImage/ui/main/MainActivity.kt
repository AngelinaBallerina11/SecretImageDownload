package com.angelina.andronova.secretImage.ui.main

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.test.espresso.IdlingResource
import com.angelina.andronova.secretImage.App
import com.angelina.andronova.secretImage.R
import com.angelina.andronova.secretImage.di.modules.ViewModelFactory
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    var idling: SimpleIdlingResource? = null
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        viewModel.screenState.observe(this, Observer {
            when (it) {
                is ScreenState.Loading -> idling?.setIdleState(false)
                is ScreenState.Idle, is Error -> {
                    if (idling != null && !idling!!.isIdleNow)
                        idling?.setIdleState(true)
                }
            }
        })
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        (application as App).preferenceRepository
            .nightModeLive.observe(this, Observer { nightMode ->
            nightMode?.let { delegate.localNightMode = it }
        }
        )
    }

    @VisibleForTesting
    fun getIdlingResource(): IdlingResource {
        if (idling == null) {
            idling = SimpleIdlingResource()
        }
        return idling!!
    }
}

/**
 * Used in Espresso testing to wait for the network response
 */
class SimpleIdlingResource : IdlingResource {

    @Volatile
    private var callback: IdlingResource.ResourceCallback? = null

    private val atomicBoolean = AtomicBoolean(true)

    override fun getName(): String = this.javaClass.name

    override fun isIdleNow(): Boolean = atomicBoolean.get()

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.callback = callback
    }

    fun setIdleState(newIdle: Boolean) {
        atomicBoolean.set(newIdle)
        if (newIdle && callback != null) {
            callback!!.onTransitionToIdle()
        }
    }
}
