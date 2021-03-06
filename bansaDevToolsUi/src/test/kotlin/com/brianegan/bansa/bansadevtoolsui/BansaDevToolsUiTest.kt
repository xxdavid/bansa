package com.brianegan.bansa.bansaDevToolsUi

import android.app.Activity
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.brianegan.bansa.Action
import com.brianegan.bansa.Reducer
import com.brianegan.bansa.Store
import com.brianegan.bansaDevTools.DevToolsStore
import com.brianegan.bansaDevToolsUi.BansaDevToolsPresenter
import com.brianegan.bansaDevToolsUi.R
import org.assertj.android.api.Assertions.assertThat
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(LOLLIPOP),
        packageName = "com.brianegan.bansaDevToolsUi",
        manifest = "src/main/AndroidManifest.xml")
class BansaDevToolsUiTest {
    @Test
    fun `ui should have a heading`() {
        val parent = createDevToolsView();
        val view = parent.findViewById(R.id.time_travel_heading)

        assertThat(view).isVisible;
    }

    @Test
    fun `ui should have a SeekBar that responds to actions`() {
        val parent = createDevToolsView();
        val view = parent.findViewById(R.id.time_travel_seek_bar) as SeekBar
        val store = createDevToolsStore() as DevToolsStore<TestState>
        val testAction = TestAction("test")
        val presenter = BansaDevToolsPresenter<TestState>(store)
        presenter.bind(parent)

        store.dispatch(testAction)

        assertThat(view).isVisible;
        Assertions.assertThat(view.max).isEqualTo(1);
    }

    @Test
    fun `ui should have a button that resets the dev tools state`() {
        val parent = createDevToolsView();
        val reset = parent.findViewById(R.id.time_travel_reset)
        val seekBar = parent.findViewById(R.id.time_travel_seek_bar) as SeekBar
        val store = createDevToolsStore() as DevToolsStore<TestState>
        val testAction = TestAction("test")
        val presenter = BansaDevToolsPresenter<TestState>(store)
        presenter.bind(parent)

        store.dispatch(testAction)

        assertThat(reset).isVisible;
        Assertions.assertThat(seekBar.max).isEqualTo(1);

        reset.performClick();
        Assertions.assertThat(seekBar.max).isEqualTo(0);
        Assertions.assertThat(store.state.msg).isEqualTo("default");
    }

    @Test
    fun `ui should have a save button`() {
        val parent = createDevToolsView();
        val save = parent.findViewById(R.id.time_travel_save)
        val seekBar = parent.findViewById(R.id.time_travel_seek_bar) as SeekBar
        val store = createDevToolsStore() as DevToolsStore<TestState>
        val testAction = TestAction("test")
        val presenter = BansaDevToolsPresenter<TestState>(store)
        presenter.bind(parent)

        store.dispatch(testAction)

        assertThat(save).isVisible;
        Assertions.assertThat(seekBar.max).isEqualTo(1);

        save.performClick();
        Assertions.assertThat(seekBar.max).isEqualTo(0);
        Assertions.assertThat(store.state.msg).isEqualTo("test");
    }

    @Test
    fun `ui should print action as they're fired`() {
        val parent = createDevToolsView();
        val view = parent.findViewById(R.id.time_travel_action) as TextView
        val store = createDevToolsStore()
        val testAction1 = TestAction("test")
        val testAction2 = TestAction("test2")
        val presenter = BansaDevToolsPresenter<TestState>(store)
        presenter.bind(parent)

        store.dispatch(testAction1)

        assertThat(view).isVisible;
        assertThat(view).hasText(testAction1.toString())

        store.dispatch(testAction2)
        assertThat(view).hasText(testAction2.toString())
    }

    @Test
    fun `should be able to unbind the presenter`() {
        val parent = createDevToolsView();
        val view = parent.findViewById(R.id.time_travel_action) as TextView
        val seekBar = parent.findViewById(R.id.time_travel_seek_bar) as SeekBar
        val store = createDevToolsStore()
        val testAction1 = TestAction("test")
        val testAction2 = TestAction("test2")
        val testAction3 = TestAction("test3")
        val presenter = BansaDevToolsPresenter<TestState>(store)
        presenter.bind(parent)

        store.dispatch(testAction1)

        assertThat(view).isVisible;
        assertThat(view).hasText(testAction1.toString())

        store.dispatch(testAction2)
        assertThat(view).hasText(testAction2.toString())

        presenter.unbind()
        store.dispatch(testAction3)
        assertThat(view).hasText(testAction2.toString())
        Assertions.assertThat(seekBar.max).isEqualTo(2)
    }

    private fun createActivity(): Activity {
        return Robolectric.setupActivity(Activity::class.java);
    }

    private fun createDevToolsView(activity: Activity = createActivity()): View {
        val frameLayout = FrameLayout(activity)
        return LayoutInflater.from(frameLayout.context).inflate(R.layout.bansa_dev_tools, frameLayout);
    }

    private fun createDevToolsStore(): Store<TestState> {
        return DevToolsStore(TestState(), TestReducer())
    }

    data class TestState(val msg: String = "default");
    data class TestAction(val msg: String) : Action

    private class TestReducer : Reducer<TestState> {
        override fun reduce(state: TestState, action: Action): TestState {
            when (action) {
                is TestAction -> return state.copy(action.msg)
                else -> return state
            }
        }
    }
}
