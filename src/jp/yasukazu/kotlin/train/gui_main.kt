package jp.yasukazu.kotlin.train

import java.awt.BorderLayout
import java.util.*
import javax.swing.*
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultTreeModel

/**
 * GUI main test
 * Created by Yasukazu on 2016/12/06.
 */
fun main(args:Array<String>){
    val btree = BinarySearchTree<Int>()
    print("\n")
    val argString = "5,2,12,8, 10,9, 7,21,19,25"
    val keys = argString.split(",") //replace("""[ \s]""", "").) //arrayOf( "B", "B", "A", "A", "C", "B1", "A", "B2", "A2", "A1")
    keys.forEach { s ->
        val x = s.trim()
        val i : Int? =
                try {
                    x.toInt()
                }
                catch(e: IllegalFormatException){
                    null
                }
        if (i != null){
            val inserted_pos = btree.insert(i)
            val pos = when(inserted_pos) {
                BinarySearchTree.InsertedPos.LEFT -> "Left"
                BinarySearchTree.InsertedPos.RIGHT -> "Right"
                BinarySearchTree.InsertedPos.NONE -> "None"
                BinarySearchTree.InsertedPos.NEW -> "New"
            }
            println("$x insert result: $pos")
        }
    }
    println()
    print("The smallest key is:")
    val key_or_null = btree.min // getMin()
    if (key_or_null == null)
        println("(null)")
    else
        println("$key_or_null")
    println()
    print("The largest key is: ${btree.max}")
    println()
    print("pre-order traversal list: ")
    val preOrderTraversalList = btree.preTraverseList()
    preOrderTraversalList.forEach { print("$it\t") }
    println()
    for(x in preOrderTraversalList)
        print("$x\n")
    println()
    println("pre-order traversal")
    btree.preTraverse { x -> print("$x\n") }
    println()
    println("pre-order traversal with depth")
    btree.preTraverse_depth("", {s,d->println("$s:$d")})

    println()
    println("post order traversal with depth")
    btree.postTraverse_depth {s,d->println("$s:$d")}

    println("\nBTree in-order traversal:")
    btree.inTraverse { x ->
        println(x)
        return@inTraverse true
    }
    println()
    print("\nBTree reverse in-order traversal:\n")
    btree.inTraverse(reverse = true, callback={ x ->
        println(x)
        return@inTraverse true
    })
    println()
    println("\nBTree interrupt in-order traversal:")
    var count = 2
    btree.inTraverse(reverse = true, callback={ x ->
        // writeInt(x)
        println(x)
        return@inTraverse --count > 0 //if(count <= 0) true else false
    })
    println()
    // try catch version
    class InterruptException: Exception()
    count = 2
    try {
        btree.inTraverse { x ->
            println(x)
            if(--count <= 0)
                throw InterruptException()
            return@inTraverse true
        }
    }
    catch(e: InterruptException){}
    println()
    print("\nBTree in-order traversal with depth:\n")
    btree.inOrderTraverse {s,d->println("$s:$d")}
    print("\n")
    val find_keys = arrayOf(5, 4, 3)//"A", "B", "B1", "B2", "C")
    find_keys.forEach { x ->
        println(if(btree.find(x)) "$x exists" else "$x does not exist")
    }
    //val delete_value = 12 //"C"

    var root = btree.preTraverseTreeNode() //DefaultMutableTreeNode("root")
    var model = DefaultTreeModel(root)
    val msgLabel = JLabel()
    val msgLabel2 = JLabel()
    val tree = JTree(model)
    tree.addTreeSelectionListener { e ->
        val node = tree.lastSelectedPathComponent
        if (node != null){
            msgLabel.text = node.toString()
            val sb = StringBuilder()
            for (path in  e.paths)
                sb.append("$path,")
            msgLabel2.text = sb.toString()
        }
    }
            val borderLayout = BorderLayout()

    //val panel = JPanel()
    val notifyLabel = JLabel("Blank leaf is left leaf.")
    //panel.add(notifyLabel)
    for(i in 0 .. tree.rowCount-1)
        tree.expandRow(i)
    //panel.add(tree)
    val button = JButton("Delete item")// $delete_value")
    button.addActionListener {
        val delete_value: Int? = try{ msgLabel.text.toInt() } catch(e: IllegalFormatException){ null }
        if(delete_value != null) {
            btree.deleteKey(delete_value)
            root = btree.preTraverseTreeNode()
            model = DefaultTreeModel(root)
            tree.model = model
            for (i in 0..tree.rowCount - 1)
                tree.expandRow(i)
        }
    }
    val msgPanel = JPanel()
    msgPanel.layout = BoxLayout(msgPanel, BoxLayout.PAGE_AXIS)
    msgPanel.add(msgLabel)
    msgPanel.add(msgLabel2)
    //panel.add(button)

    SwingUtilities.invokeLater {
        val frame = JFrame()//"Binary Search Tree")
        //frame.contentPane = binaryTreeForm.panel1
        frame.contentPane.layout = borderLayout
        frame.contentPane.add(notifyLabel, BorderLayout.NORTH)
        frame.contentPane.add(tree, BorderLayout.CENTER)
        frame.contentPane.add(msgPanel, BorderLayout.SOUTH)
        frame.contentPane.add(button, BorderLayout.EAST)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }
}
