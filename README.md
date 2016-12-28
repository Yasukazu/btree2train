# Binary Search Tree structure and algorithm as Computer Science
 - Usage:
```kotlin
import jp.yasukazu.kotlin.train.BinarySearchTreeModel
import jp.yasukazu.kotlin.train.IntBinarySearchTreeFrame
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args:Array<String>){
    val treeModel = BinarySearchTreeModel<Int>()
    SwingUtilities.invokeLater {
        val intTreeFrame = IntBinarySearchTreeFrame(treeModel)
        with(intTreeFrame){
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            isVisible = true
        }
    }
}
```