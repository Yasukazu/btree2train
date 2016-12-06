package jp.yasukazu.kotlin.train

import java.util.*
import javax.swing.*
import javax.swing.tree.DefaultTreeModel

/**
 * CUI main test
 * Created by Yasukazu on 2016/12/06.
 */
fun main(args:Array<String>){
    fun print_depth(s:String, d:Int){
        var i = d
        val b = StringBuilder()
        while(i-- > 0){
            b.append(".")
        }
        print("$b$s\n")
    }

    // fun print_depth_s(k: T, s:String){ println("$k:$s") }
    fun print_depth_SB(s:String, d:StringBuffer){
        print("${d.toString()}$s\n")
    }
    fun print_depth_LR(s:String, d:Int){
        var i = d
        val b = StringBuilder()
        while(i-- > 0){
            b.append(".")
        }
        print("$b$s\n")
    }
    /* val root = Node("d")
    root.insert(root, "b")
    root.insert(root, "f") */
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

    print("\nBTree in-order traversal:\n")
    /*
    fun writeInt(i: Int):Boolean {
       println(i)
        return true
    }*/
    var count = 2
    btree.inTraverse { x ->
        // writeInt(x)
        println(x)
        return@inTraverse --count > 0 //if(count <= 0) true else false
    }
    print("\nBTree in-order traversal with depth:\n")
    btree.inOrderTraverse {s,d->println("$s:$d")}
    print("\n")
    val find_keys = arrayOf(5, 4, 3)//"A", "B", "B1", "B2", "C")
    find_keys.forEach { x ->
        println(if(btree.find(x)) "$x exists" else "$x does not exist")
    }
    /* delete */
    val delete_value = 12 //"C"
    val pre_delete_list = btree.preTraverseList()
    println("Pre delete list: $pre_delete_list")
    val pre_delete_node_list = btree.preTraverseNodeList()
    for (n in pre_delete_node_list) {
        val left_key = n?.left?.key
        val right_key = n?.right?.key
        println("Key: ${n?.key}, Left-> $left_key, Right-> $right_key")
    }
    println()
    //println("Pre delete list of nodes: $pre_delete_node_list")
    println("Try to delete $delete_value")
    //val delete_result = btree.deleteKey(delete_value)
    //println("Deletr Key result: $delete_result")
    //val post_delete_list = btree.preTraverseList()
    //for (n in btree.preTraverseNodeList()) println("Key: ${n?.key}")
    //println("Post delete list: $post_delete_list")
    // val binaryTreeForm = BinaryTreeForm()
    var root = btree.preTraverseTreeNode() //DefaultMutableTreeNode("root")
    var model = DefaultTreeModel(root)
    val panel = JPanel()
    val label = JLabel("Blank leaf is left leaf.")
    panel.add(label)
    val tree = JTree(model)
    for(i in 0 .. tree.rowCount-1)
        tree.expandRow(i)
    panel.add(tree)
    val button = JButton("Delete item $delete_value")
    button.addActionListener {
        btree.deleteKey(delete_value)
        root = btree.preTraverseTreeNode()
        model = DefaultTreeModel(root)
        tree.model = model
        for(i in 0 .. tree.rowCount-1)
            tree.expandRow(i)
    }
    panel.add(button)
    SwingUtilities.invokeLater {
        val frame = JFrame("Binary Search Tree")
        //frame.contentPane = binaryTreeForm.panel1
        frame.contentPane.add(panel)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }
}