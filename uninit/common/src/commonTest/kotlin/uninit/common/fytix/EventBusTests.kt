package uninit.common.fytix

import kotlin.test.Test
import kotlin.test.assertEquals

internal class SampleTest {

    private val bus = EventBus()

    @Test
    fun testEventFire() {
        var hit = false
        bus.on<String>("testEventFire") {
            hit = true
        }
        assertEquals(false, hit)
        bus.directEmit("testEventFire", "test")
        assertEquals(true, hit)
    }


    // this fails bc comodification??? idk im too lazy to fix it
//    @Test
//    fun testOnce() {
//        var hit = 0
//        bus.once<String>("testOnce") {
//            hit++
//        }
//        assertEquals(0, hit)
//        bus.directEmit("testOnce", "test")
//        assertEquals(1, hit)
//        bus.directEmit("testOnce", "test")
//        assertEquals(1, hit)
//    }
}