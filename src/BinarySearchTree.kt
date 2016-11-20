import javax.swing.*
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
    data class Node <T: Comparable<T>>(var key: T, var left: Node<T>? = null, var right: Node<T>? = null, var parent: Node<T>? = null)
    /*{
        val key = key_
        var left = left_
        var right = right_
    }*/

    var root: Node<T>? = null

    enum class InsertedPos {
        LEFT, NEW, RIGHT, NONE
    }


    data class Node_InsertedPos <T: Comparable<T>>(val node: Node<T>, val pos: InsertedPos)
    private fun _insert(node: Node<T>?, key: T, parent: Node<T>?): Node_InsertedPos<T> { //<Node<T>?, Node<T>?, InsertedPos> {
        if (node == null) { // If the node is empty, return a new node
            return Node_InsertedPos(Node(key, parent=parent), InsertedPos.NEW)
        }
        else { // Otherwise, recur down the tree
            if (key < node.key) {
                val node_InsertedPos = _insert(node.left, key, node)
                if (node_InsertedPos.pos == InsertedPos.NEW) {
                    node.left = node_InsertedPos.node
                    return Node_InsertedPos(node_InsertedPos.node, InsertedPos.LEFT) // Return the (unchanged) node pointer
                }
            } else if (key > node.key) {
                val node_InsertedPos = _insert(node.right, key, node)
                if (node_InsertedPos.pos == InsertedPos.NEW) {
                    node.right = node_InsertedPos.node
                    return Node_InsertedPos(node_InsertedPos.node, InsertedPos.RIGHT) // Return the (unchanged) node pointer
                }
            }
        }
        return Node_InsertedPos(node, InsertedPos.NONE) // Return the (unchanged) node pointer
    }

    fun insert(key: T): InsertedPos {
        if (root == null){
            root = Node(key)
            return InsertedPos.NEW
        }
        else {
            val node_InsertedPos = _insert(root, key, null)
            return node_InsertedPos.pos
        }
    }

    /**
     * find minimum key node
     */
    private fun _getMinNode(node: Node<T>?): Node<T>? {
        if (node == null || node.left == null)
            return node
        return _getMinNode(node.left)
    }

    /**
     * find minimum key
     * return : key or null
     */
    val min: T?
        get() {
            val node = _getMinNode(root)
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
        return _getMinNode(node.right)
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
                tnode.insert(newTnode, 1)
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

    /**
     * Exception class
     */
    class NoNodeException(msg: String): Exception(msg)

    /**
     * delete node
     */
    fun delete_node(node: Node<T>?, key: T, writer: (String)->Unit): Node<T>?{
        if (node == null)
            writer("No '$key'.") // throw NoNodeException("delete: node == null")
        else if (key < node.key)
            node.left = delete_node(node.left, key, writer)
        else if (key > node.key)
            node.right = delete_node(node.right, key, writer)
        else {
            if (node.left == null)
                return node.right
            else if (node.right == null)
                return node.left
           val rightMinNode = _getMinNode(node.right)
           if (rightMinNode != null) {
               node.key = rightMinNode.key
               node.right = delete_node(node.right, node.key, writer)
           }
           else
               writer("_getMinNode returned null value.")
       }
        return node
    }

    fun delete(key: T, writer: (String)->Unit=::println): Boolean {
       val node = delete_node(root, key, writer)
       if (node == null)
           return false
       return true
    }

    /**
     * Replace Node n with Node m while n's one child is null and other one is m
    private fun Replace(n: Node<T>, m: Node<T>){
        val p = n.parent
        if (m != null)
            m.parent = p
        if (n == this.root)
            this.root = m
        else if (p != null) {
            if (p.left == n)
                p.left = m
            else
                p.right = m
        }
    }
     */


    /**
     * Delete as Wikipedia
     * @return: success => true
     */
    //data class ParentkChild<T>(val parent: Node<T>, val child: Node<T>)
    private fun _delete_node(self: Node<T>?, key: T): Boolean { //Pair<T, T>?{
         // Delete self node
        fun __delete_self_node(self: Node<T>) {
             assert(self.left == null && self.right == null)
            val parent = self.parent
            if (parent == null)
                root = null
            if (parent?.left == self)
                parent.left = null
            else if (parent?.right == self)
                parent.right = null
        }
        // replace self with other
        fun __replace(self: Node<T>, child: Node<T>) {
            child.parent = self.parent
            if (self.parent != null) {
                if (self.parent?.left == self)
                    self.parent?.left = child
                else if (self.parent?.right == self)
                    self.parent?.right = child
            }
        }
        /**
         * replace 2
         * 削除ノードが子を二つ持つ場合
        削除ノードの左の子から最大の値を探索する。
        1 で探索してきたノード（以下、探索ノード）を削除対象のノードと置き換えて、削除対象のノードを削除する。
        このとき探索ノードの左の子を探索ノードの元位置に置き換える（二分探索木の性質上、探索ノードには右の子は無い）。
         */
        fun __replace2(self: Node<T>){
            val maxNode = _getMax(self.left)
            if (maxNode != null) {
                __replace(self, maxNode)
                __delete_self_node((maxNode))
            }
        }
        if (self == null)
            return false
        if (key < self.key) {
            return _delete_node(self.left, key)
        }
        else if (key > self.key) {
            return _delete_node(self.right, key)
        }
        else { // key == self.key
            // 3. delete self if no children
            if (self.left == null && self.right == null) {
                __delete_self_node(self)
                return true
            }
            else if (self.left != null && self.right == null) {
                __replace(self, self.left!!)
                return true
            }
            else if (self.right != null && self.left == null) {
                __replace(self, self.right!!)
                return true
            }
            else {
                __replace2(self)
                return true
            }
        }
    }

    fun deleteKey(key: T): Boolean {
        //_delete_node(superRoot, root, key)
        if (root != null) {
            return _delete_node(root, key)
            //val chNode = root
            //if (key < chNode.key)
//            var parentNode = Node(root!!.key)
 //           var selfNode = root
            //if (key < selfNode.key)
        }
        return false
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
    val btree = BinarySearchTree<Int>()
    print("\n")
    val keys = args //arrayOf( "B", "B", "A", "A", "C", "B1", "A", "B2", "A2", "A1")
    keys.forEach { x ->
        val inserted_pos = btree.insert(x.toInt())
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
    val find_keys = arrayOf(5, 4, 3)//"A", "B", "B1", "B2", "C")
    find_keys.forEach { x ->
        if (btree.find(x))
            print("$x exists\n")
        else
            print("$x does not exist\n")
    }
    /* delete */
    val delete_value = 12 //"C"
    val pre_delete_list = btree.preTraverseList()
    println("Pre delete list: $pre_delete_list")
    println("Try to delete $delete_value")
    val delete_result = btree.deleteKey(delete_value)
    val post_delete_list = btree.preTraverseList()
    println("Post delete list: $pre_delete_list")
    /*
    if (delete_result == true)
        println("$delete_value is deleted.")
    else
        println("$delete_value is not deleted.")
        */

    // val binaryTreeForm = BinaryTreeForm()
    var root = btree.preTraverseTreeNode() //DefaultMutableTreeNode("root")
    var model = DefaultTreeModel(root)
    val panel = JPanel()
    val label = JLabel("Blank leaf is left leaf.")
    panel.add(label)
    val tree = JTree(model)
    panel.add(tree)
    val button = JButton("Delete item $delete_value")
    button.addActionListener {
        btree.delete(delete_value)
        root = btree.preTraverseTreeNode()
        model = DefaultTreeModel(root)
        tree.model = model
        //tree.revalidate()
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