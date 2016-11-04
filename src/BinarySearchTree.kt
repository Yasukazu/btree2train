/**
 * Created by Yasukazu on 2016/11/03.
 *  @comment_source  http://quiz.geeksforgeeks.org/binary-search-tree-set0-search-and-insertion/
 */
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
    fun _getMin(node: Node<T>?): Node<T>? {
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


    fun traverse(node:Node<T>?, callback:(T)->Unit){
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

    fun display(node:Node<T>?, depth:Int, callback:(Int, T)->Unit){
        if (node == null)
            return
        else {
            display(node.left, depth + 1, callback)
            callback(depth, node.key)
            display(node.right, depth + 1, callback)
        }
    }

    fun display_all(callback:(Int, T)->Unit){
        display(root, 0, callback)
    }
}

fun main(args:Array<String>){
    fun print_depth(d:Int, s:String){
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
    print("Minimum key is:")
    val key_or_null = btree.min // getMin()
    if (key_or_null == null)
        println("(null)")
    else
        println("$key_or_null")
    print("\nBTree traverse:\n")
    btree.traverse_all {x -> print("$x\n") }
    print("\nBTree display with depth:\n")
    btree.display_all(::print_depth) // btree.root, 0,
    print("\n")
    val find_keys = arrayOf("A", "B", "B1", "B2", "C")
    find_keys.forEach { x ->
        if (btree.find(x))
            print("$x exists\n")
        else
            print("$x does not exist\n")
    }
}