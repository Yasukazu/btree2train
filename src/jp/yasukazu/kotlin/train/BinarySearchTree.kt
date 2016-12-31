package jp.yasukazu.kotlin.train

import java.util.NoSuchElementException
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.MutableTreeNode

//import javax.swing.tree.TreeNode

/**
 * Created by Yasukazu@GitHub on 2016/11/03.
 *  @comment_source  http://quiz.geeksforgeeks.org/binary-search-tree-set0-search-and-insertion/
 */

/**
 * class for Tree BinaryNode
 * @property key
 */
open class BinaryNode<T: Comparable<T>> (var key: T, var left: BinaryNode<T>? = null, var right: BinaryNode<T>? = null){
    override fun toString():String{
        return "$key:(${left?.key}, ${right?.key})"
    }

    operator fun get(i: Int): BinaryNode<T>? {
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

open class BinarySearchTree <T: Comparable<T>> : Iterable<T> {

    var rootBinaryNode: BinaryNode<T>? = null
    private var _size: Int = 0
    val size: Int get() = _size

    enum class InsertedPos {
        LEFT, NEW, RIGHT, NONE
    }

    data class Node_InsertedPos <T: Comparable<T>>(val binaryNode: BinaryNode<T>, val pos: InsertedPos)
    private fun _insert(binaryNode: BinaryNode<T>?, key: T): Node_InsertedPos<T> { //<BinaryNode<T>?, BinaryNode<T>?, InsertedPos> {
        if (binaryNode == null) { // If the binaryNode is empty, return a new binaryNode
            return Node_InsertedPos(BinaryNode(key), InsertedPos.NEW)
        }
        else { // Otherwise, recur down the tree
            if (key < binaryNode.key) {
                val node_InsertedPos = _insert(binaryNode.left, key)
                if (node_InsertedPos.pos == InsertedPos.NEW) {
                    ++_size
                    binaryNode.left = node_InsertedPos.binaryNode
                    return Node_InsertedPos(node_InsertedPos.binaryNode, InsertedPos.LEFT) // Return the (unchanged) binaryNode pointer
                }
            } else if (key > binaryNode.key) {
                val (newNode, pos) = _insert(binaryNode.right, key)
                if (pos == InsertedPos.NEW) {
                    ++_size
                    binaryNode.right = newNode
                    return Node_InsertedPos(newNode, InsertedPos.RIGHT) // Return the (unchanged) binaryNode pointer
                }
            }
        }
        return Node_InsertedPos(binaryNode, InsertedPos.NONE) // Return the (unchanged) binaryNode pointer
    }

    fun insert_(key: T): InsertedPos {
        if (rootBinaryNode == null){
            rootBinaryNode = BinaryNode(key)
            ++_size
            return InsertedPos.NEW
        }
        else {
            val node_InsertedPos = _insert(rootBinaryNode, key)
            return node_InsertedPos.pos
        }
    }


    fun insert(key: T): InsertedPos {
        class _BreakException(val e: InsertedPos): Exception()
        var insertedPos = InsertedPos.NONE
        tailrec fun _insert(node: BinaryNode<T>) {//: BinaryNode<T>, node: BinaryNode<T>?, pos: InsertedPos){
            if (node.key == key)
                throw _BreakException(InsertedPos.NONE)
            when (node.childrenStatus) {
                0 -> {
                    if (key < node.key) {
                        node.left = BinaryNode(key)
                        //insertedPos = InsertedPos.LEFT
                        throw _BreakException(InsertedPos.LEFT)
                    } else {//if (key > node.key) {
                        node.right = BinaryNode(key)
                        //insertedPos = InsertedPos.RIGHT
                        throw _BreakException(InsertedPos.RIGHT)
                    }
                }
                1 -> {
                    if (key > node.key){
                        node.right = BinaryNode(key)
                        //insertedPos = InsertedPos.RIGHT
                        throw _BreakException(InsertedPos.RIGHT)
                    } else
                        _insert(node.left!!)
                }
                2 -> {
                    if (key < node.key){
                        node.left = BinaryNode(key)
                        //insertedPos = InsertedPos.LEFT
                        throw  _BreakException(InsertedPos.LEFT)
                    } else
                        _insert(node.right!!)
                }
                3 -> {
                    if (key < node.key)
                        _insert(node.left!!)
                    else
                        _insert(node.right!!)
                }
            }
        }

        if (rootBinaryNode == null){
            rootBinaryNode = BinaryNode(key)
            ++_size
            return InsertedPos.NEW
        }
        else {
            try {
                _insert(rootBinaryNode!!)
            } catch (e: _BreakException) {
                insertedPos = e.e
            }
            if (insertedPos != InsertedPos.NONE)
                ++_size
            return insertedPos
        }
    }

    operator fun plusAssign(key: T) { insert(key) }


    /**
     * find minimum key binaryNode
     */
    tailrec fun _getMinNode(binaryNode: BinaryNode<T>?): BinaryNode<T>? {
        if (binaryNode == null || binaryNode.left == null)
            return binaryNode
        return _getMinNode(binaryNode.left)
    }

    /**
     * find minimum key
     * return : key or null
     */
    val min: T?
        get() {
            val node = _getMinNode(rootBinaryNode)
            if (node != null) {
                return node.key
            }
            return null
        }

    /**
     * find maximum key binaryNode
     */
    tailrec fun _getMaxNode(binaryNode: BinaryNode<T>?): BinaryNode<T>? {
        if (binaryNode == null || binaryNode.right == null)
            return binaryNode
        return _getMaxNode(binaryNode.right)
    }

    /**
     * find maximum key
     * return : key or null
     */
    val max: T?
        get() {
            val node = _getMaxNode(rootBinaryNode)
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
        tailrec fun _find(binaryNode: BinaryNode<T>?): BinaryNode<T>? {
            if (binaryNode == null) {
                return null
            } else { // Otherwise, recur down the tree
                if (key < binaryNode.key) {
                    return _find(binaryNode.left)
                } else if (key > binaryNode.key) {
                    return _find(binaryNode.right)
                }
            }
            return binaryNode
        }
        return _find(rootBinaryNode) != null
    }

    /**
     * in operator
     */
    operator fun contains(key: T) = find(key)

    /**
     * find key with path
     * @return : null if no match
     */
    fun findPath(key: T): List<BinaryNode<T>>?{
        val path = mutableListOf<BinaryNode<T>>()//List<T>
        /**
         * pre-find
         * return non-null if found
         */
        tailrec fun _find(binaryNode: BinaryNode<T>?): BinaryNode<T>? {
            if (binaryNode == null) {
                //throw NoMatchException()
                path.clear()
                return null
            } else { // Otherwise, recur down the tree
                path.add(binaryNode)//path.size, binaryNode)
                if (key < binaryNode.key) {
                    return _find(binaryNode.left)
                } else if (key > binaryNode.key) {
                    return _find(binaryNode.right)
                }else {
                    return binaryNode
                }
            }
        }
        //try { val lastNode =
                    _find(rootBinaryNode)
            //assert(if(path.size > 0)lastNode == path[path.size - 1] else true)
            return if(path.size > 0) path else null
        //} catch (e: NoMatchException) return null
    }

    fun preTraverseNodeList(): List<BinaryNode<T>?>{
        fun _preTraverseList(binaryNode: BinaryNode<T>?, list: MutableList<BinaryNode<T>?>){
            if (binaryNode == null)
                return
            else {
                list += binaryNode
                _preTraverseList(binaryNode.left, list)
                _preTraverseList(binaryNode.right, list)
            }
        }
        val list = mutableListOf<BinaryNode<T>?>()
        _preTraverseList(rootBinaryNode, list)
        return list
    }

    private fun _preTraverseList(binaryNode: BinaryNode<T>?, list: MutableList<T>){
        if (binaryNode == null)
            return
        else {
            list += binaryNode.key
            _preTraverseList(binaryNode.left, list)
            _preTraverseList(binaryNode.right, list)
        }
    }

    fun preTraverseList(): List<T>{
        val list = mutableListOf<T>()
        _preTraverseList(rootBinaryNode, list)
        return list
    }

    private fun _preTraverse(binaryNode: BinaryNode<T>?, callback:(T)->Unit){
        if (binaryNode == null)
            return
        else {
            callback(binaryNode.key)
            _preTraverse(binaryNode.left, callback)
            _preTraverse(binaryNode.right, callback)
        }
    }

    fun preTraverse(callback:(T)->Unit){
        _preTraverse(rootBinaryNode, callback)
    }

    tailrec fun _preTraverseTreeNode(binaryNode: BinaryNode<T>?, tnode: MutableTreeNode){
        if (binaryNode == null)
            return
        else {
            var newTnode: DefaultMutableTreeNode? = null
            if (binaryNode.left !=null) {
                newTnode = DefaultMutableTreeNode(binaryNode.left?.key)
                tnode.insert(newTnode, 0)
                _preTraverseTreeNode(binaryNode.left, newTnode)
            } else if (binaryNode.right !=null) {
                if (newTnode == null)
                    tnode.insert(DefaultMutableTreeNode(null), 0)
                newTnode = DefaultMutableTreeNode(binaryNode.right?.key)
                tnode.insert(newTnode, 1)
                _preTraverseTreeNode(binaryNode.right, newTnode)
            }
        }
    }

    fun preTraverseTreeNode(): MutableTreeNode?{
        if (rootBinaryNode != null) {
            val treeNode = DefaultMutableTreeNode(rootBinaryNode!!.key)
            _preTraverseTreeNode(rootBinaryNode, treeNode)
            return treeNode
        }
        return null
    }

    private fun _preTraverse_depth(binaryNode: BinaryNode<T>?, depth: String, callback:(T, String)->Unit){
        if (binaryNode == null)
            return
        else {
            callback(binaryNode.key, depth)
            _preTraverse_depth(binaryNode.left, depth + "<", callback)
            _preTraverse_depth(binaryNode.right, depth + ">", callback)
        }
    }

    fun preTraverse_depth(s: String, callback:(T, String)->Unit){
       _preTraverse_depth(rootBinaryNode, s, callback)
    }


    private fun _postTraverse_depth(binaryNode: BinaryNode<T>?, depth: String, callback:(T, String)->Unit){
        if (binaryNode == null)
            return
        else {
            _postTraverse_depth(binaryNode.left, depth + "<", callback)
            _postTraverse_depth(binaryNode.right, depth + ">",  callback)
            callback(binaryNode.key, depth)
        }
    }

    fun postTraverse_depth(callback:(T, String)->Unit){
        _postTraverse_depth(rootBinaryNode, "", callback)
    }

    //enum class LR {L, }
    fun preTraverse_depth_LR(callback:(T, Int, Char)->Unit){
        fun _preTraverse_depth_LR(binaryNode: BinaryNode<T>?, callback:(T, Int, Char)->Unit, depth: Int, LR: Char){
            if (binaryNode == null)
                return
            else {
                callback(binaryNode.key, depth, LR)
                _preTraverse_depth_LR(binaryNode.left, callback, depth + 1, '<')
                _preTraverse_depth_LR(binaryNode.right, callback, depth + 1, '>')
            }
        }
        _preTraverse_depth_LR(rootBinaryNode, callback, 0, '_')
    }

    /**
     * Depth Map
     */
    fun traverseDepthList(): MutableMap<Int,Int> {
        val depthMap: MutableMap<Int,Int> =mutableMapOf()
        fun traverseDepth(binaryNode: BinaryNode<T>?, depth: Int){
            if (binaryNode == null)
                return
            else {
                if (depthMap.containsKey(depth)) {
                    val d = depthMap[depth]
                    if (d != null)
                        depthMap[depth] = d + 1 //?.plus(1)// = depthMap[depth] + 1
                }
                else
                    depthMap[depth] = 1
                traverseDepth(binaryNode.left, depth + 1)
                traverseDepth(binaryNode.right, depth + 1)
            }
        }
        traverseDepth(rootBinaryNode, 0)
        return depthMap
    }

    /**
     * Leaf count map
     */
    fun traverseLeafCount(): MutableMap<Int,Int> {
        val map: MutableMap<Int,Int> =mutableMapOf()
        fun countUp(binaryNode: BinaryNode<T>?){
            if (binaryNode == null)
                return
            else {
                var count = 0
                count += if (binaryNode.left != null) 1 else 0
                count += if (binaryNode.right != null) 1 else 0
                if (map.containsKey(count)) {
                    val d = map[count]
                    if (d != null)
                        map[count] = d + 1
                }
                else
                    map[count] = 1
                countUp(binaryNode.left)
                countUp(binaryNode.right)
            }
        }
        countUp(rootBinaryNode)
        return map
    }

    /**
     * in-order traverse from rootBinaryNode
     * key is fed to [callback] function
     * interrupt break if callback returns false
     *
     */
    fun inTraverse(reverse:Boolean=false, callback: (T) -> Boolean){
        //val list = MutableList<T>()
        class _InterrruptException() :Exception()
        fun _inTraverse(binaryNode: BinaryNode<T>?){
            if (binaryNode == null)
                return
            else {
                _inTraverse(binaryNode.left)
                if(!callback(binaryNode.key))// <= 0)
                    throw _InterrruptException()
                _inTraverse(binaryNode.right)
            }
        }
        fun _reverseInTraverse(binaryNode: BinaryNode<T>?){
            if (binaryNode == null)
                return
            else {
                _reverseInTraverse(binaryNode.right)
                if(!callback(binaryNode.key)) // <= 0)
                    throw _InterrruptException()
                _reverseInTraverse(binaryNode.left)
            }
        }
        try {
            if (reverse)
                _reverseInTraverse(rootBinaryNode)
            else
                _inTraverse(rootBinaryNode)
        }
        catch (e: _InterrruptException){
        }
    }


    private fun _inOrderTraverse(binaryNode: BinaryNode<T>?, depth: String, callback: (T, String)->Unit){
        if (binaryNode == null)
            return
        else {
            _inOrderTraverse(binaryNode.left, depth + "<", callback)
            callback(binaryNode.key, depth)
            _inOrderTraverse(binaryNode.right, depth + ">", callback)
        }
    }

    fun inOrderTraverse(callback: (T, String)->Unit){
        _inOrderTraverse(rootBinaryNode, "", callback)
    }




    /**
     * Delete a binaryNode following the procedure written in Wikipedia
     * @return success => true
     */
    enum class DeleteResult {SELF_DELETE, L_REPLACE, R_REPLACE, SUCC_REPLACE, NO_MATCH, EMPTY}
    fun delete(key: T): DeleteResult {
        //class ImproperArgumentException(msg:String) : Exception(msg)
        fun _delete_node(self: BinaryNode<T>?, parent: BinaryNode<T>?): DeleteResult { //Pair<T, T>?{
            // Delete self binaryNode
            fun __delete_self_node(self: BinaryNode<T>, parent: BinaryNode<T>?){
                assert(self.left == null && self.right == null)
                //val parent = self.parent
                if (parent == null)
                    rootBinaryNode = null
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
            fun __replace(self: BinaryNode<T>, child: BinaryNode<T>, parent: BinaryNode<T>?){
                if (parent == null) {
                    rootBinaryNode = child
                } else {
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
            2) * Name the binaryNode with the value to be deleted as 'N binaryNode'.  Without deleting N binaryNode, after choosing its
            in-order successor binaryNode (R binaryNode), copy the value of R to N.
             */
            fun __replace2(self: BinaryNode<T>){
                assert(self.left != null && self.right != null)
                //if(self.left == null || self.right == null)
                //    throw ImproperArgumentException("Both child are not null!")
                //else {
                    //data class Node_Parent(val self: BinaryNode<T>, val parent: BinaryNode<T>)
                    fun getPredecessorNode(_self: BinaryNode<T>, _parent: BinaryNode<T>) : Pair<BinaryNode<T>, BinaryNode<T>> {//binaryNode: BinaryNode<T>?, parent: BinaryNode<T>?
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
                        predParent.right = predNode.left // delete maximum-value binaryNode
                    else if(predParent.left == predNode) // No need for this code
                        predParent.left = predNode.left // Never reach here
                //}
            }
            // code starts here
            if (self == null)
                return DeleteResult.NO_MATCH
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
                        0 -> {
                            __delete_self_node(self, parent)
                            return DeleteResult.SELF_DELETE
                        }
                        1 -> {
                            __replace(self, self.left!!, parent)
                            return DeleteResult.L_REPLACE
                        }
                        2 -> {
                            __replace(self, self.right!!, parent)
                            return DeleteResult.R_REPLACE
                        }
                        else -> {
                            __replace2(self)
                            return DeleteResult.SUCC_REPLACE
                        }
                    }
                }
            }
        }
        if (rootBinaryNode != null) {
            val deleted = _delete_node(rootBinaryNode, null)
            if (deleted != DeleteResult.NO_MATCH){
                --_size
            }
            return deleted
        }
        return DeleteResult.EMPTY
    }

    /**
     * -= operator
     */
    operator fun minusAssign(k: T){ delete(k)}

    /**
     * Iterable
     */
    override operator fun iterator(): Iterator<T>{
        return NodeIterator()
    }

    /**
     * NodeIterator
     */
    inner class NodeIterator: Iterator<T> {
        val list = mutableListOf<T>()
        var n = 0

        init {
            inTraverse { k ->
                list.add(k)
                return@inTraverse true
            }
        }

        override fun hasNext(): Boolean {
            return list.size > n
        }

        override fun next(): T {
            if(n >= list.size)
                throw NoSuchElementException()
            return list[n++]
        }
    }
}

