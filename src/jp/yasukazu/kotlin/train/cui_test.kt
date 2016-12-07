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
    println()

    println("BTree in-order traversal:")
    btree.inTraverse { x ->
        println(x)
        return@inTraverse true
    }
    println()
    println("BTree reverse in-order traversal:")
    btree.inTraverse(reverse = true, callback={ x ->
        println(x)
        return@inTraverse true
    })
    println()
    println("BTree break in-order traversal:")
    var count = 2
    btree.inTraverse { x ->
        // writeInt(x)
        println(x)
        return@inTraverse --count > 0 //if(count <= 0) true else false
    }
    println()

    println("BTree break reverse in-order traversal:")
    count = 2
    btree.inTraverse(reverse = true, callback = { x ->
        // writeInt(x)
        println(x)
        return@inTraverse --count > 0 //if(count <= 0) true else false
    })
    println()
    println("BTree break in-order traversal(try catch version):")
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
    println("Pre delete list of nodes: $pre_delete_node_list")
    println("Try to delete $delete_value")
    val delete_result = btree.deleteKey(delete_value)
    println("Delete Key result: $delete_result")
    val post_delete_list = btree.preTraverseList()
    for (n in btree.preTraverseNodeList()) println("Key: ${n?.key}")
    println("Post delete list: $post_delete_list")

}