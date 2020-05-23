package io.github.wulkanowy.utils

import com.google.firebase.iid.FirebaseInstanceId
import java.util.concurrent.CountDownLatch

fun getFirebaseToken(): String {
    val lock = CountDownLatch(1)
    var token = ""
    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
        if (it.isSuccessful) {
            token = it.result!!.token
        }
        lock.countDown()
    }
    lock.await()
    return token
}
