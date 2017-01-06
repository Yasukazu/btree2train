# Binary Search Tree structure and algorithm as Computer Science
 
Usage
-----
### Quick Trial ###

 + by Jython

   1. inside jython, Use downloaded "btree2train.jar" : lines start with # are comments
   ```python
   import sys
   sys.path.append('Downloads/btree2train.jar')
   # new object
   bst = BinarySearchTree()
   # know properties/methods
   dir(bst)
   # get size
   bst.size
   # insert keys
   bst.insert('b')
   bst.insert('c')
   bst.insert('a')
   bst.size
   # iterate
   for n in bst:
     print(n)
   # check existence
   'b' in bst
   # delete key
   bst.delete(3)
   # get node list
   nodeList = bst.preTraverseNodeList()
   # import any number class to use number
   from java.lang import Double
   # create a new binary search tree
   bst2 = BinarySearchTree()
   # insert number into bst2
   bst2.insert(Double(0.5))
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
    val treeModel = BinarySearchTreeModel<Int>()
    args.forEach { a ->
        val i = try { a.toInt() } catch (pos: NumberFormatException){
            println("$a caused $pos")
            null }
        if (i != null){
            treeModel += i
        }
    }
    SwingUtilities.invokeLater {
        val intTreeFrame = BinarySearchTreeFrame(treeModel) { it.toInt() }
        with(intTreeFrame){
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            pack()
            isVisible = true
        }
    }
}
```
