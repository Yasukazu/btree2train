package jp.yasukazu.kotlin.train

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode

//import javax.swing.tree.TreeNode

/**
 * Created by Yasukazu on 2016/11/03.
 *  @comment_source  http://quiz.geeksforgeeks.org/binary-search-tree-set0-search-and-insertion/
 */
//import kotlin.collection.co

open class BinarySearchTree <T: Comparable<T>> {
    /**
     * @property key
     */
    class Node <T: Comparable<T>>(var key: T, var left: Node<T>? = null, var right: Node<T>? = null){
        override fun toString():String{
            return "$key:(${left?.key}, ${right?.key})"
        }

        operator fun get(i: Int): Node<T>? {
            return when(i){
                0 -> left
                else -> right
            }
        }

        val size: Int get(){
            return (if (this[0] != null) 1 else 0) + (if (this[1] != null) 1 else 0)
        }

        val childrenStatus: Int get(){
            return (if (this[0] != null) 1 else 0) + (if (this[1] != null) 2 else 0)
        }
    }

    var rootNode: Node<T>? = null

    enum class InsertedPos {
        LEFT, NEW, RIGHT, NONE
    }

    data class Node_InsertedPos <T: Comparable<T>>(val node: Node<T>, val pos: InsertedPos)
    private fun _insert(node: Node<T>?, key: T): Node_InsertedPos<T> { //<Node<T>?, Node<T>?, InsertedPos> {
        if (node == null) { // If the node is empty, return a new node
            return Node_InsertedPos(Node(key), InsertedPos.NEW)
        }
        else { // Otherwise, recur down the tree
            if (key < node.key) {
                val node_InsertedPos = _insert(node.left, key)
                if (node_InsertedPos.pos == InsertedPos.NEW) {
                    node.left = node_InsertedPos.node
                    return Node_InsertedPos(node_InsertedPos.node, InsertedPos.LEFT) // Return the (unchanged) node pointer
                }
            } else if (key > node.key) {
                val (n, pos) = _insert(node.right, key)
                if (pos == InsertedPos.NEW) {
                    node.right = n
                    return Node_InsertedPos(n, InsertedPos.RIGHT) // Return the (unchanged) node pointer
                }
            }
        }
        return Node_InsertedPos(node, InsertedPos.NONE) // Return the (unchanged) node pointer
    }

    fun insert(key: T): InsertedPos {
        if (rootNode == null){
            rootNode = Node(key)
            return InsertedPos.NEW
        }
        else {
            val node_InsertedPos = _insert(rootNode, key)
            return node_InsertedPos.pos
        }
    }

    operator fun plusAssign(key: T) { insert(key) }


    /**
     * find minimum key node
     */
    tailrec fun _getMinNode(node: Node<T>?): Node<T>? {
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
            val node = _getMinNode(rootNode)
            if (node != null) {
                return node.key
            }
            return null
        }

    /**
     * find maximum key node
     */
    tailrec fun _getMaxNode(node: Node<T>?): Node<T>? {
        if (node == null || node.right == null)
            return node
        return _getMaxNode(node.right)
    }

    /**
     * find maximum key
     * return : key or null
     */
    val max: T?
        get() {
            val node = _getMaxNode(rootNode)
            if (node != null) {
                return node.key
            }
            return null
        }


    class NoMatchException : Exception()

    /**
     * find key
     */
    fun find(key: T):Boolean{
        /**
         * pre-find
         * return non-null if found
         */
        tailrec fun _find(node: Node<T>?): Node<T>? {
            if (node == null) {
                return null
            } else { // Otherwise, recur down the tree
                if (key < node.key) {
                    return _find(node.left)
                } else if (key > node.key) {
                    return _find(node.right)
                }
            }
            return node
        }
        return if(_find(rootNode) != null)
            true
        else false
    }

    /**
     * find key with path
     * @return : null if no match
     */
    fun findPath(key: T): List<Node<T>>?{
        val path = mutableListOf<Node<T>>()//List<T>
        /**
         * pre-find
         * return non-null if found
         */
        tailrec fun _find(node: Node<T>?): Node<T>? {
            if (node == null) {
                //throw NoMatchException()
                path.clear()
                return null
            } else { // Otherwise, recur down the tree
                path.add(node)//path.size, node)
                if (key < node.key) {
                    return _find(node.left)
                } else if (key > node.key) {
                    return _find(node.right)
                }else {
                    return node
                }
            }
        }
        //try {
            val lastNode = _find(rootNode)
            assert(lastNode == path[path.size - 1])
            return if(path.size > 0) path else null
        //} catch (e: NoMatchException) return null
    }

    fun preTraverseNodeList(): List<Node<T>?>{
        fun _preTraverseList(node: Node<T>?, list: MutableList<Node<T>?>){
            if (node == null)
                return
            else {
                list += node
                _preTraverseList(node.left, list)
                _preTraverseList(node.right, list)
            }
        }
        val list = mutableListOf<Node<T>?>()
        _preTraverseList(rootNode, list)
        return list
    }

    private fun _preTraverseList(node: Node<T>?, list: MutableList<T>){
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
        _preTraverseList(rootNode, list)
        return list
    }

    private fun _preTraverse(node: Node<T>?, callback:(T)->Unit){
        if (node == null)
            return
        else {
            callback(node.key)
            _preTraverse(node.left, callback)
            _preTraverse(node.right, callback)
        }
    }

    fun preTraverse(callback:(T)->Unit){
        _preTraverse(rootNode, callback)
    }

    tailrec fun _preTraverseTreeNode(node: Node<T>?, tnode: MutableTreeNode){
        if (node == null)
            return
        else {
            var newTnode: DefaultMutableTreeNode? = null
            if (node.left !=null) {
                newTnode = DefaultMutableTreeNode(node.left?.key)
                tnode.insert(newTnode, 0)
                _preTraverseTreeNode(node.left, newTnode)
            } else if (node.right !=null) {
                if (newTnode == null)
                    tnode.insert(DefaultMutableTreeNode(null), 0)
                newTnode = DefaultMutableTreeNode(node.right?.key)
                tnode.insert(newTnode, 1)
                _preTraverseTreeNode(node.right, newTnode)
            }
        }
    }

    fun preTraverseTreeNode(): MutableTreeNode?{
        if (rootNode != null) {
            val treeNode = DefaultMutableTreeNode(rootNode!!.key)
            _preTraverseTreeNode(rootNode, treeNode)
            return treeNode
        }
        return null
    }

    private fun _preTraverse_depth(node: Node<T>?, depth: String, callback:(T, String)->Unit){
        if (node == null)
            return
        else {
            callback(node.key, depth)
            _preTraverse_depth(node.left, depth + "<", callback)
            _preTraverse_depth(node.right, depth + ">", callback)
        }
    }

    fun preTraverse_depth(s: String, callback:(T, String)->Unit){
       _preTraverse_depth(rootNode, s, callback)
    }


    private fun _postTraverse_depth(node: Node<T>?, depth: String, callback:(T, String)->Unit){
        if (node == null)
            return
        else {
            _postTraverse_depth(node.left, depth + "<", callback)
            _postTraverse_depth(node.right, depth + ">",  callback)
            callback(node.key, depth)
        }
    }

    fun postTraverse_depth(callback:(T, String)->Unit){
        _postTraverse_depth(rootNode, "", callback)
    }

    //enum class LR {L, }
    fun preTraverse_depth_LR(callback:(T, Int, Char)->Unit){
        fun _preTraverse_depth_LR(node: Node<T>?, callback:(T, Int, Char)->Unit, depth: Int, LR: Char){
            if (node == null)
                return
            else {
                callback(node.key, depth, LR)
                _preTraverse_depth_LR(node.left, callback, depth + 1, '<')
                _preTraverse_depth_LR(node.right, callback, depth + 1, '>')
            }
        }
        _preTraverse_depth_LR(rootNode, callback, 0, '_')
    }

    /**
     * Depth Map
     */
    fun traverseDepthList(): MutableMap<Int,Int> {
        val depthMap: MutableMap<Int,Int> =mutableMapOf()
        fun traverseDepth(node: Node<T>?, depth: Int){
            if (node == null)
                return
            else {
                if (depthMap.containsKey(depth)) {
                    val d = depthMap[depth]
                    if (d != null)
                        depthMap[depth] = d + 1 //?.plus(1)// = depthMap[depth] + 1
                }
                else
                    depthMap[depth] = 1
                traverseDepth(node.left, depth + 1)
                traverseDepth(node.right, depth + 1)
            }
        }
        traverseDepth(rootNode, 0)
        return depthMap
    }

    /**
     * Leaf count map
     */
    fun traverseLeafCount(): MutableMap<Int,Int> {
        val map: MutableMap<Int,Int> =mutableMapOf()
        fun countUp(node: Node<T>?){
            if (node == null)
                return
            else {
                var count = 0
                count += if (node.left != null) 1 else 0
                count += if (node.right != null) 1 else 0
                if (map.containsKey(count)) {
                    val d = map[count]
                    if (d != null)
                        map[count] = d + 1
                }
                else
                    map[count] = 1
                countUp(node.left)
                countUp(node.right)
            }
        }
        countUp(rootNode)
        return map
    }

    /**
     * in-order traverse from rootNode
     * key is fed to [callback] function
     * interrupt break if callback returns false
     *
     */
    fun inTraverse(reverse:Boolean=false, callback: (T) -> Boolean){
        //val list = MutableList<T>()
        class _InterrruptException() :Exception()
        fun _inTraverse(node: Node<T>?){
            if (node == null)
                return
            else {
                _inTraverse(node.left)
                if(!callback(node.key))// <= 0)
                    throw _InterrruptException()
                _inTraverse(node.right)
            }
        }
        fun _reverseInTraverse(node: Node<T>?){
            if (node == null)
                return
            else {
                _reverseInTraverse(node.right)
                if(!callback(node.key)) // <= 0)
                    throw _InterrruptException()
                _reverseInTraverse(node.left)
            }
        }
        try {
            if (reverse)
                _reverseInTraverse(rootNode)
            else
                _inTraverse(rootNode)
        }
        catch (e: _InterrruptException){
        }
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
        _inOrderTraverse(rootNode, "", callback)
    }

    /**
     * Exception class
     */
    //class NoNodeException(msg: String): Exception(msg)

    /**
     * delete node
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
       val node = delete_node(rootNode, key, writer)
       return if (node == null)
           false
       else true
    }

     */


    /**
     * Delete a node following the procedure written in Wikipedia
     * @return success => true
     */
    fun deleteKey(key: T): Boolean {
        //class ImproperArgumentException(msg:String) : Exception(msg)
        fun _delete_node(self: Node<T>?, parent: Node<T>?): Boolean { //Pair<T, T>?{
            // Delete self node
            fun __delete_self_node(self: Node<T>, parent: Node<T>?){
                assert(self.left == null && self.right == null)
                //val parent = self.parent
                if (parent == null)
                    rootNode = null
                else {
                    if (parent.left == self)
                        parent.left = null
                    else if (parent.right == self)
                        parent.right = null
                    else
                        assert(true, {"Unable to delete self: Parent has no self!"})
                }
            }
            // replace self with child
            fun __replace(self: Node<T>, child: Node<T>, parent: Node<T>?){
                //child.parent = parent
                if (parent != null) {
                    if (parent.left == self)
                        parent.left = child
                    else if (parent.right == self)
                        parent.right = child
                    else
                        assert(true, {"Unable to replace self with a child: Parent has no self!"})
                }
            }
            /**
             * replace 2
            2) * Name the node with the value to be deleted as 'N node'.  Without deleting N node, after choosing its
            in-order successor node (R node), copy the value of R to N.
             */
            fun __replace2(self: Node<T>){
                assert(self.left != null && self.right != null)
                //if(self.left == null || self.right == null)
                //    throw ImproperArgumentException("Both child are not null!")
                //else {
                    //data class Node_Parent(val self: Node<T>, val parent: Node<T>)
                    fun getPredecessorNode( _self: Node<T>, _parent: Node<T>) : Pair<Node<T>,Node<T>> {//node: Node<T>?, parent: Node<T>?
                        var s = _self
                        var p = _parent
                        while (s.right != null) {
                            p = s // Reserve the parent first
                            s = s.right!!
                        }
                        return Pair(s, p)
                    }
                    val (predNode, predParent) = getPredecessorNode(self.left!!, self) // in-order predecessor
                    self.key = predNode.key
                    if(predParent.right == predNode)
                        predParent.right = predNode.left // delete maximum-value node
                    else if(predParent.left == predNode) // No need for this code
                        predParent.left = predNode.left // Never reach here
                //}
            }
            // code starts here
            if (self == null)
                return false
            else {
                if (key < self.key) {
                    return _delete_node(self.left, self)
                } else if (key > self.key) {
                    return _delete_node(self.right, self)
                } else { // key == self.key
                    // 3. delete self if no children
                    val bL = if(self.left == null) 0 else 1
                    val bR = if(self.right == null) 0 else 2
                    when(bL or bR){
                        0 -> __delete_self_node(self, parent)
                        1 -> __replace(self, self.left!!, parent)
                        2 -> __replace(self, self.right!!, parent)
                        else -> __replace2(self)
                    }
                    return true
                    /*
                    if (self.left == null && self.right == null) {
                        return __delete_self_node(self, parent)
                    } else if (hl == 0b10){//(self.left != null && self.right == null) {
                        return __replace(self, self.left!!, parent)
                    } else if (self.right != null && self.left == null) {
                        return __replace(self, self.right!!, parent)
                    } else { // Node has 2 children
                        return __replace2(self)
                    } */
                }
            }
        }
        if (rootNode != null) {
            return _delete_node(rootNode, null)
        }
        return false
    }
}

