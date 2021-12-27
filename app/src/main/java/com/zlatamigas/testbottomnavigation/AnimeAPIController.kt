package com.zlatamigas.testbottomnavigation

class AnimeAPIController {
    suspend fun getAnimes(searchString: String): Location =

        // Create a new coroutine that can be cancelled
        suspendCancellableCoroutine<Location> { continuation ->

            // Add listeners that will resume the execution of this coroutine
            lastLocation.addOnSuccessListener { location ->
                // Resume coroutine and return location
                continuation.resume(location)
            }.addOnFailureListener { e ->
                // Resume the coroutine by throwing an exception
                continuation.resumeWithException(e)
            }

            // End of the suspendCancellableCoroutine block. This suspends the
            // coroutine until one of the callbacks calls the continuation parameter.
        }
}