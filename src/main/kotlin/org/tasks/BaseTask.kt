package org.tasks

abstract class BaseTask(
    private var name: String = "Un-named"
) {
    abstract fun shouldExcecute(): Boolean
    abstract fun run()
}