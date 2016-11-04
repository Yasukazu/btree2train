/**
 * Created by Yasukazu on 2016/11/03.
 *  @comment_source  http://quiz.geeksforgeeks.org/binary-search-tree-set0-search-and-insertion/
 */
class BinarySearchTree <T: Comparable<T>> {
    /**
     * @property key
     */
    class Node <T: Comparable<T>>(key_: T, left_: Node<T>? = null, right_: Node<T>? = null) {
        val key = key_
        var left = left_
        var right = right_

        /* override fun toString():String{
            return key + ":L(" + (left ? left.toString() : "Null") +
        }  */

        fun add(key: T):Boolean{
            if(key == this.key)
                return false
            else{
                if(key < this.key) {
                    if (this.left == null) {
                        left = Node(key)
                        return true
                    } else
                        return left!!.add(key)
                }else if(key > this.key){
                    if(this.right == null){
                        right = Node(key)
                        return true
                    } else
                        return right!!.add(key)
                }
            }
            return false
        }

        /**
         * Insert a [key]
         * @param node:Node?
         * @param key
         * @return inserted place as Node object
         */
        fun insert(node: Node<T>?, key: T): Node<T>? {
            if (node == null) { // If the tree is empty, return a new node
                return Node(key, null, null)
            } else { // Otherwise, recur down the tree
                if (key < node.key) {
                    node.left = insert(node.left, key)
                } else if (key > node.key) {
                    node.right = insert(node.right, key)
                }
            }
            return node // Return the (unchanged) node pointer
        }

        fun find(node: Node<T>?, key: T):Node<T>?{
            if(node == null){
                return null
            }
            if(key < node.key){
                return find(node.left, key)
            }
            else if(key > node.key){
                return find(node.right, key)
            }
            return node
        }
    }
    var root: Node<T>? = null

    fun insert0(key: T){
        if(root is Node){
            (root as Node).insert(root, key)
        }
        else {
            root = Node(key)
        }
    }

    /* fun add(key: T):Boolean{
        if(root == null){
            root = Node(key)
            return true
        }
        else{
            val new_node = _insert(root, key)
            if (new_node != null)
                return true
        }
        return false
    }      */

    fun _insert(node: Node<T>?, key: T): Pair<Node<T>?, Boolean> {
        if (node == null) { // If the tree is empty, return a new node
            return Pair(Node(key, null, null), true)
        } else { // Otherwise, recur down the tree
            if (key < node.key) {
                val pair = _insert(node.left, key)
                node.left = pair.first
            } else if (key > node.key) {
                val pair = _insert(node.right, key)
                node.right = pair.first
            }
        }
        return Pair(node, false) // Return the (unchanged) node pointer
    }

    fun insert(key: T): Boolean {
        val pair = _insert(root, key)
        if (root == null)
            root = pair.first
        return pair.second
    }

    /**
     * pre-find
     * return non-null if found
     */
    fun _find(node: Node<T>?, key: T): Node<T>? {
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

    /* fun find(key: String):Boolean{
        if(find(root, key) != null)
            return true
        return false
    }  */

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
    val keys = arrayOf( "B", "A", "C", "B1")
    keys.forEach { x ->
        btree.insert(x)
    }
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