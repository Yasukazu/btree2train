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


    private fun _preTraverse(node:Node<T>?, callback:(T)->Unit){
        if (node == null)
            return
        else {
            _preTraverse(node.left, callback)
            _preTraverse(node.right, callback)
            callback(node.key)
        }
    }

    fun preTraverse(callback:(T)->Unit){
        _preTraverse(root, callback)
    }

    private fun _preTraverse_depth(node:Node<T>?, callback:(T, StringBuffer)->Unit, depth: StringBuffer){
        if (node == null)
            return
        else {
            _preTraverse_depth(node.left, callback, depth.append('<'))
            _preTraverse_depth(node.right, callback, depth.append('>'))
            callback(node.key, depth)
            depth.setLength(0)
        }
    }

    fun preTraverse_depth(callback:(T, StringBuffer)->Unit){
       _preTraverse_depth(root, callback, StringBuffer())
    }


    private fun _postTraverse_depth(node:Node<T>?, callback:(T, StringBuffer)->Unit, depth: StringBuffer){
        if (node == null)
            return
        else {
            callback(node.key, depth)
            _postTraverse_depth(node.left, callback, depth.append('<'))
            _postTraverse_depth(node.right, callback, depth.append('>'))
            depth.setLength(0)
        }
    }

    fun postTraverse_depth(callback:(T, StringBuffer)->Unit){
        _postTraverse_depth(root, callback, StringBuffer())
    }

    //enum class LR {L, }
    private fun _preTraverse_depth_LR(node:Node<T>?, callback:(T, Int, Char)->Unit, depth: Int, LR: Char){
        if (node == null)
            return
        else {
            _preTraverse_depth_LR(node.left, callback, depth + 1, '<')
            _preTraverse_depth_LR(node.right, callback, depth + 1, '>')
            callback(node.key, depth, LR)
        }
    }

    fun preTraverse_depth_LR(callback:(T, Int, Char)->Unit){
        _preTraverse_depth_LR(root, callback, 0, '_')
    }
    private fun traverse(node:Node<T>?, callback:(T)->Unit){
        if (node == null)
            return
        else {
            traverse(node.left, callback)
            callback(node.key)
            traverse(node.right, callback)
        }
    }

    fun traverse_all(callback:(T)->Unit){
        traverse(root, callback)
    }


    private fun display(node:Node<T>?, callback:(T, StringBuffer)->Unit, depth:StringBuffer){
        if (node == null)
            return
        else {
            display(node.left, callback, depth.append('<'))
            callback(node.key, depth)
            display(node.right, callback, depth.append('>'))
            depth.setLength(0)
        }
    }

    fun display_all(callback:(T, StringBuffer)->Unit){
        display(root, callback, StringBuffer())
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

    fun print_depth_SB(s:String, d:StringBuffer){
        print("${d.toString()}$s\n")
    }
    fun print_depth_LR(s:String, d:Int, c:Char){
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
    val keys = arrayOf( "B", "B", "A", "A", "C", "B1", "A")
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
    println("pre order traversal")
    btree.preTraverse { x -> print("$x\n") }
    println()
    println("pre order traversal with depth")
    btree.preTraverse_depth(::print_depth_SB)

    println()
    println("post order traversal with depth")
    btree.postTraverse_depth(::print_depth_SB)

    print("\nBTree traverse:\n")
    btree.traverse_all {x -> print("$x\n") }
    print("\nBTree display with depth:\n")
    btree.display_all(::print_depth_SB) // btree.root, 0,
    print("\n")
    val find_keys = arrayOf("A", "B", "B1", "B2", "C")
    find_keys.forEach { x ->
        if (btree.find(x))
            print("$x exists\n")
        else
            print("$x does not exist\n")
    }
}