import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeNode

/**
 * Created by Yasukazu on 2016/11/03.
 *  @comment_source  http://quiz.geeksforgeeks.org/binary-search-tree-set0-search-and-insertion/
 */
//import kotlin.collection.co

class BinarySearchTree <T: Comparable<T>> {
    /**
     * @property key
     */
    data class Node <T: Comparable<T>>(val key: T, var left: Node<T>? = null, var right: Node<T>? = null)
    /*{
        val key = key_
        var left = left_
        var right = right_
    }*/

    var root: Node<T>? = null

    enum class InsertedPos {
        LEFT, NEW, RIGHT, NONE
    }

    private fun _insert(node: Node<T>?, key: T): Pair<Node<T>?, InsertedPos> {
        if (node == null) { // If the tree is empty, return a new node
            return Pair(Node(key, null, null), InsertedPos.NEW)
        } else { // Otherwise, recur down the tree
            if (key < node.key) {
                val pair = _insert(node.left, key)
                if (pair.second == InsertedPos.NONE)
                    return Pair(node, InsertedPos.NONE)
                node.left = pair.first
                return Pair(node, InsertedPos.LEFT) // Return the (unchanged) node pointer
            } else if (key > node.key) {
                val pair = _insert(node.right, key)
                if (pair.second == InsertedPos.NONE)
                    return Pair(node, InsertedPos.NONE)
                node.right = pair.first
                return Pair(node, InsertedPos.RIGHT) // Return the (unchanged) node pointer
            }
            else
                return Pair(node, InsertedPos.NONE) // Return the (unchanged) node pointer
        }
    }

    fun insert(key: T): InsertedPos {
        val pair = _insert(root, key)
        if (root == null)
            root = pair.first
        return pair.second
    }

    /**
     * find minimum key
     */
    private fun _getMin(node: Node<T>?): Node<T>? {
        if (node == null || node.left == null)
            return node
        return _getMin(node.left)
    }

    /**
     * find minimum key
     * return : key or null
     */
    /*fun getMin(): T? {
        val node = _getMin(root)
        if (node != null) {
            return node.key
        }
        return null
    }*/
    val min: T?
        get() {
            val node = _getMin(root)
            if (node != null) {
                return node.key
            }
            return null
        }

    /**
     * find maximum key
     */
    private fun _getMax(node: Node<T>?): Node<T>? {
        if (node == null || node.right == null)
            return node
        return _getMin(node.right)
    }

    /**
     * find maximum key
     * return : key or null
     */
    val max: T?
        get() {
            val node = _getMax(root)
            if (node != null) {
                return node.key
            }
            return null
        }
    /**
     * pre-find
     * return non-null if found
     */
    private fun _find(node: Node<T>?, key: T): Node<T>? {
        if (node == null) {
            return null
        } else { // Otherwise, recur down the tree
            if (key < node.key) {
                return _find(node.left, key)
            } else if (key > node.key) {
                return _find(node.right, key)
            }
        }
        return node
    }

    fun find(key: T):Boolean{
        if(_find(root, key) != null)
            return true
        return false
    }



    private fun _preTraverseList(node:Node<T>?, list: MutableList<T>){
        if (node == null)
            return
        else {
            list += node.key
            _preTraverseList(node.left, list)
            _preTraverseList(node.right, list)
        }
    }

    fun preTraverseList(): List<T>{
        val list = mutableListOf<T>()
        _preTraverseList(root, list)
        return list
    }

    private fun _preTraverse(node:Node<T>?, callback:(T)->Unit){
        if (node == null)
            return
        else {
            callback(node.key)
            _preTraverse(node.left, callback)
            _preTraverse(node.right, callback)
        }
    }

    fun preTraverse(callback:(T)->Unit){
        _preTraverse(root, callback)
    }

    private fun _preTraverseTreeNode(node:Node<T>?, tnode: MutableTreeNode){
        if (node == null)
            return
        else {
            var newTnode: DefaultMutableTreeNode? = null
            if (node.left !=null) {
                newTnode = DefaultMutableTreeNode(node.left?.key)
                tnode.insert(newTnode, 0)
                _preTraverseTreeNode(node.left, newTnode)
            }
            if (node.right !=null) {
                if (newTnode == null)
                    tnode.insert(DefaultMutableTreeNode(null), 0)
                newTnode = DefaultMutableTreeNode(node.right?.key)
                tnode.insert(newTnode, 0)
                _preTraverseTreeNode(node.right, newTnode)
            }
        }
    }

    fun preTraverseTreeNode(): MutableTreeNode?{
        if (root != null) {
            val treeNode = DefaultMutableTreeNode(root!!.key)
            _preTraverseTreeNode(root, treeNode)
            return treeNode
        }
        return null
    }
    private fun _preTraverse_depth(node:Node<T>?, depth: String, callback:(T, String)->Unit){
        if (node == null)
            return
        else {
            callback(node.key, depth)
            _preTraverse_depth(node.left, depth + "<", callback)
            _preTraverse_depth(node.right, depth + ">", callback)
        }
    }

    fun preTraverse_depth(s: String, callback:(T, String)->Unit){
       _preTraverse_depth(root, s, callback)
    }


    private fun _postTraverse_depth(node:Node<T>?, depth: String, callback:(T, String)->Unit){
        if (node == null)
            return
        else {
            _postTraverse_depth(node.left, depth + "<", callback)
            _postTraverse_depth(node.right, depth + ">",  callback)
            callback(node.key, depth)
        }
    }

    fun postTraverse_depth(callback:(T, String)->Unit){
        _postTraverse_depth(root, "", callback)
    }

    //enum class LR {L, }
    private fun _preTraverse_depth_LR(node:Node<T>?, callback:(T, Int, Char)->Unit, depth: Int, LR: Char){
        if (node == null)
            return
        else {
            callback(node.key, depth, LR)
            _preTraverse_depth_LR(node.left, callback, depth + 1, '<')
            _preTraverse_depth_LR(node.right, callback, depth + 1, '>')
        }
    }

    fun preTraverse_depth_LR(callback:(T, Int, Char)->Unit){
        _preTraverse_depth_LR(root, callback, 0, '_')
    }
    fun _inTraverse(node:Node<T>?, callback:(T)->Unit){
        if (node == null)
            return
        else {
            _inTraverse(node.left, callback)
            callback(node.key)
            _inTraverse(node.right, callback)
        }
    }

    fun inTraverse(callback:(T)->Unit){
        _inTraverse(root, callback)
    }


    private fun _inOrderTraverse(node: Node<T>?, depth: String, callback: (T, String)->Unit){
        if (node == null)
            return
        else {
            _inOrderTraverse(node.left, depth + "<", callback)
            callback(node.key, depth)
            _inOrderTraverse(node.right, depth + ">", callback)
        }
    }

    fun inOrderTraverse(callback: (T, String)->Unit){
        _inOrderTraverse(root, "", callback)
    }
}

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
    val btree = BinarySearchTree<String>()
    print("\n")
    val keys = args //arrayOf( "B", "B", "A", "A", "C", "B1", "A", "B2", "A2", "A1")
    keys.forEach { x ->
        val inserted_pos = btree.insert(x)
        val pos = when(inserted_pos) {
            BinarySearchTree.InsertedPos.LEFT -> "Left"
            BinarySearchTree.InsertedPos.RIGHT -> "Right"
            BinarySearchTree.InsertedPos.NONE -> "None"
            BinarySearchTree.InsertedPos.NEW -> "New"
        }
        println("$x insert result: $pos")
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
    btree.inTraverse { x -> print("$x\n") }
    print("\nBTree in-order traversal with depth:\n")
    btree.inOrderTraverse {s,d->println("$s:$d")}
    print("\n")
    val find_keys = arrayOf("A", "B", "B1", "B2", "C")
    find_keys.forEach { x ->
        if (btree.find(x))
            print("$x exists\n")
        else
            print("$x does not exist\n")
    }
    // val binaryTreeForm = BinaryTreeForm()
    val root = btree.preTraverseTreeNode() //DefaultMutableTreeNode("root")
    val model = DefaultTreeModel(root)
    val tree = JTree(model)
    SwingUtilities.invokeLater {
        val frame = JFrame("Binary Search Tree")
        //frame.contentPane = binaryTreeForm.panel1
        frame.contentPane.add(JLabel("Blank leaf is left leaf."))
        frame.contentPane.add(tree)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
    }
}