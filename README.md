# Binary Search Tree structure and algorithm as Computer Science
Usage
-----
 - [Wiki](https://github.com/Yasukazu/btree2train/wiki)
### Quick Trial ###

 + by Kotlin REPL

   1. inside kotlinc.bat, Use downloaded "btree2train.jar" : lines start with # are comments
   
```PowerShell
  > C:\kotlinc\bin\kotlinc.bat -classpath .\IdeaProjects\btree2train\out\artifacts\btree2train_jar2\btree2train.jar 
```
 - inside kotlin REPL: 
 Welcome to Kotlin version 1.0.5-2 (JRE 1.8.0_111-b14)
 Type :help for help, :quit for quit
```kotlin
import jp.yasukazu.kotlin.train.tree.*
val node: SearchBinaryNode<Int> = BasicSearchBinaryNode(5)
node.add(3)
//-> (LEFT, BasicSearchBinaryNode: 5(3, null))// (BasicSearchBinaryNode: 3(null, null), LEFT, BasicSearchBinaryNode: 5(3, null))
node.preTraverseDepth {n,d -> println("$d:$n")}
//-> 5 at NONE under null
//-> 3 at LEFT under 5
``` 
     
 - GUI Usage:
```kotlin
/**
 * Created by Yasukazu on 2016/12/29.
 * GUI main outside from the package
 * 2016-12-29 BinarySearchTreeFrame (accepts any Comparable type)
 */
import jp.yasukazu.kotlin.train.BinarySearchTreeModel
import jp.yasukazu.kotlin.train.BinarySearchTreeFrame
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args:Array<String>){
    val floatTreeModel = BinarySearchTreeModel<Int>()
    args.forEach { a ->
        val key = try { a.toInt() } catch (pos: NumberFormatException){
            println("$a caused $pos")
            null }
        if (key != null){
            floatTreeModel += key
        }
    }
    SwingUtilities.invokeLater {
        val intTreeFrame = BinarySearchTreeFrame(floatTreeModel) { it.toInt() }
        with(intTreeFrame){
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            pack()
            isVisible = true
        }
    }
}
```
