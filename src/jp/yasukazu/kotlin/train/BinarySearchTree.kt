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

    /*
    inner class ChildrenIterator: Iterator<BinaryNode<T>?>{
        var list = mutableListOf<BinaryNode<T>>()
        var n = 0
        init {
            when(childrenStatus){
                0 -> {}
                1 -> list.add(left!!)
                2 -> { list.add(right!!) }
                else -> { list.add(left!!)
                    list.add(right!!) }
            }
        }
        override fun hasNext(): Boolean {
            return list.size > n
        }

        override fun next(): BinaryNode<T>? {
            if (n >= list.size)
                throw NoSuchElementException()
            return list[n++]
        }
    }

    override operator fun iterator(): Iterator<BinaryNode<T>>{ return ChildrenIterator() }
    */

    /**
     * iterCount count
     */
    val size: Int
        get(){
        return (if (this[0] != null) 1 else 0) + (if (this[1] != null) 1 else 0)
    }

    val childrenStatus: Int get(){
        return (if (this[0] != null) 1 else 0) or (if (this[1] != null) 2 else 0)
    }

    /**
     * recursive count self and iterCount
     */
    val count: Int
        get(){
            var n = 0
            fun _count(node: BinaryNode<T>?){
                if (node == null)
                    return
                ++n
                if (node.left != null)
                    _count(node.left)
                if (node.right != null)
                    _count(node.right)
            }
            _count(this)
            return n
        }

    fun copy(): BinaryNode<T>?{
        fun _copy(node: BinaryNode<T>?): BinaryNode<T>?{
            if (node == null)
                return null
           val newNode = BinaryNode(node.key, node.left, node.right)
            if (node.left != null)
                newNode.left = _copy(node.left)
            if (node.right != null)
                newNode.right = _copy(node.right)
            return newNode
        }
        return _copy(this)
    }
}

enum class InsertedPos {
    LEFT, NEW, RIGHT, NONE
}

enum class DeleteResult {SELF_DELETE, LEFT_REPLACE, RIGHT_REPLACE, PREDEC_REPLACE, NO_MATCH, EMPTY}

interface InsertDeletable<in T> {
    fun insert(key: T): InsertedPos
    fun delete(key: T): DeleteResult
}

/**
 * interface(like Object Pascal)
 */
interface BasicBinarySearchTree<T: Comparable<T>> {
    val size: Int
    fun insert(key: T): InsertedPos
    fun delete(key: T): DeleteResult
    fun preTraverse(callback: (T)->Unit)
    fun inTraverse(callback: (T)->Unit)
    fun postTraverse(callback: (T)->Unit)
}

/**
 * implementation: implemented items cap 'override'
 */
open class BinarySearchTree <T: Comparable<T>> : Iterable<T>, InsertDeletable<T>, BasicBinarySearchTree<T>  {

    var rootNode: BinaryNode<T>? = null
    private var _size: Int = 0
    override val size: Int get() = count()//_size
    private val serialVersionUID = 45456465444654634L


    fun forceSetSize(i: Int?){
        if(i != null)
            _size = i
    }

    data class Node_InsertedPos <T: Comparable<T>>(val binaryNode: BinaryNode<T>, val pos: InsertedPos)
    fun insert_(key: T): InsertedPos {
        fun _insert(binaryNode: BinaryNode<T>?, key: T): Node_InsertedPos<T> { //<BinaryNode<T>?, BinaryNode<T>?, InsertedPos> {
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

        if (rootNode == null){
            rootNode = BinaryNode(key)
            ++_size
            return InsertedPos.NEW
        }
        else {
            val node_InsertedPos = _insert(rootNode, key)
            return node_InsertedPos.pos
        }
    }


    open class InsertDeleteException: Exception()
    open class InsertException: InsertDeleteException()
    class InsertFailException: InsertException()
    open class DeleteException: InsertDeleteException()
    class DeleteFailException: DeleteException()
    class DeleteSuccessException(val result: DeleteResult): DeleteException()
    /**
     * insert
     * @parak key
     * @return InsertedPos
     * @throws InsertFailException
     */
    override fun insert(key: T): InsertedPos {
        class _BreakException(val pos: InsertedPos): Exception()
        var insertedPos = InsertedPos.NONE
        tailrec fun _insert(node: BinaryNode<T>) {//: BinaryNode<T>, node: BinaryNode<T>?, pos: InsertedPos){
            if (node.key < key || node.key > key)
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
            else
                throw InsertFailException()
        }

        if (rootNode == null){
            rootNode = BinaryNode(key)
            ++_size
            return InsertedPos.NEW
        }
        else {
            try {
                _insert(rootNode!!)
            } catch (e: _BreakException) {
                insertedPos = e.pos
            }
            //if (insertedPos != InsertedPos.NONE)
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
            val node = _getMinNode(rootNode)
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
    enum class FindResult {NOT_FOUND, FOUND}
    fun find(key: T):Boolean{
        class _BreakException(val e: FindResult): Exception()
        /**
         * pre-find
         * return non-null if found
         */
        var result = false
        tailrec fun _find(binaryNode: BinaryNode<T>?){//: BinaryNode<T>? {
            if (binaryNode == null) {
                //result = false
                throw _BreakException(FindResult.NOT_FOUND)
                //return null
            } else { // Otherwise, recur down the tree
                if (key < binaryNode.key) {
                    _find(binaryNode.left)
                } else if (key > binaryNode.key) {
                    _find(binaryNode.right)
                } else {
                    //result = true
                    throw _BreakException(FindResult.FOUND)
                }
            }
        }
        try {
           _find(rootNode)
        } catch (e: _BreakException){
            result = e.e == FindResult.FOUND
        }
        return result
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
                    _find(rootNode)
            //assert(if(path.size > 0)lastNode == path[path.size - 1] else true)
            return if(path.size > 0) path else null
        //} catch (e: NoMatchException) return null
    }

    fun preTraverseNodeList(): List<BinaryNode<T>?>{
        val list = mutableListOf<BinaryNode<T>?>()
        fun _preTraverseList(binaryNode: BinaryNode<T>?){
            if (binaryNode == null)
                return
            else {
                list += binaryNode
                _preTraverseList(binaryNode.left)
                _preTraverseList(binaryNode.right)
            }
        }
        _preTraverseList(rootNode)
        return list
    }

    fun preTraverseList(): List<T>{
        val list = mutableListOf<T>()
        fun _preTraverseList(binaryNode: BinaryNode<T>?){
            if (binaryNode == null)
                return
            else {
                list += binaryNode.key
                _preTraverseList(binaryNode.left)
                _preTraverseList(binaryNode.right)
            }
        }
        _preTraverseList(rootNode)
        return list
    }

    /**
     * count up keys
     */
    fun count(): Int{
        return rootNode?.count ?: 0
    }

    /**
     * copy/clone self
     */
    open fun copy(): BinarySearchTree<T>?{
       if (rootNode == null)
           return null
        val newRoot = rootNode?.copy()
        val newTree = BinarySearchTree<T>()
        newTree.rootNode = newRoot
        newTree._size = this.count()
        return newTree
    }


    override fun preTraverse(callback: (T)->Unit){//Boolean){
        //class _BreakException: Exception()
        fun _preTraverse(node: BinaryNode<T>?){
            if (node == null)
                return
            else {
               // if (!
                callback(node.key)
                    //throw _BreakException()
                _preTraverse(node.left)
                _preTraverse(node.right)
            }
        }
        //try {
            _preTraverse(rootNode)
        /*
        }
        catch (e: _BreakException){

        } */
    }

    fun preTraversePrint(callback:(String)->Unit){
        fun _preTraverse(binaryNode: BinaryNode<T>?){
            if (binaryNode == null)
                return
            else {
                callback("${binaryNode.key}:(")// callback("(")//Left recur starts;")
                _preTraverse(binaryNode.left)
                callback(",")//Right recur starts;")
                _preTraverse(binaryNode.right)
                callback(")")
            }
        }
        _preTraverse(rootNode)
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
        if (rootNode != null) {
            val treeNode = DefaultMutableTreeNode(rootNode!!.key)
            _preTraverseTreeNode(rootNode, treeNode)
            return treeNode
        }
        return null
    }

    fun preTraverse_depth(s: String, callback:(T, String)->Unit){
        fun _preTraverse_depth(binaryNode: BinaryNode<T>?, depth: String){
            if (binaryNode == null)
                return
            else {
                callback(binaryNode.key, depth)
                _preTraverse_depth(binaryNode.left, depth + "<")
                _preTraverse_depth(binaryNode.right, depth + ">")
            }
        }
       _preTraverse_depth(rootNode, s)
    }

    enum class TraverseLR {NONE, L, R}
    fun preTraverseLR(callback:(T?, TraverseLR)->Unit){
        fun _preTraverseLR(binaryNode: BinaryNode<T>?, L_R: TraverseLR){
            if (binaryNode == null)
                callback(null, L_R)
            else {
                callback(binaryNode.key, L_R)
                _preTraverseLR(binaryNode.left, TraverseLR.L)
                _preTraverseLR(binaryNode.right, TraverseLR.R)
            }
        }
        _preTraverseLR(rootNode, TraverseLR.NONE)
    }

    /**
     * post order traverse
     * @param callback: like fun(k: T?) = println("$k")
     */
    override fun postTraverse(callback:(T)->Unit){
        fun _postTraverse(node: BinaryNode<T>?){
            if (node == null)
                return
            else {
                _postTraverse(node.left)
                _postTraverse(node.right)
                callback(node.key)
            }
        }

        _postTraverse(rootNode)
    }

    fun postTraverse_depth(callback:(T?, String)->Unit){
        fun _postTraverse_depth(node: BinaryNode<T>?, depth: String){
            if (node == null)
                callback(null, depth)
            else {
                _postTraverse_depth(node.left, depth + "+")
                _postTraverse_depth(node.right, depth + "+")
                callback(node.key, depth)
            }
        }

        _postTraverse_depth(rootNode, "")
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
        _preTraverse_depth_LR(rootNode, callback, 0, '_')
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
        traverseDepth(rootNode, 0)
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
        countUp(rootNode)
        return map
    }

    /**
     * in-order traverse from rootNode
     * key is fed to [callback] function
     */
    override fun inTraverse(callback: (T) -> Unit){
        fun _inTraverse(node: BinaryNode<T>?){
            if (node == null)
                return
            else {
                _inTraverse(node.left)
                callback(node.key)
                _inTraverse(node.right)
            }
        }
        _inTraverse(rootNode)
    }

    /**
     * reverse in-order traverse from rootNode
     * @param callback: key is fed to [callback] function
     */
    fun reverseInTraverse(callback: (T) -> Unit){
        fun _reverseInTraverse(node: BinaryNode<T>?){
            if (node == null)
                return
            else {
                _reverseInTraverse(node.right)
                callback(node.key)
                _reverseInTraverse(node.left)
            }
        }
        _reverseInTraverse(rootNode)
    }

    /**
     * get nth of in-order traverse from rootNode
     * @param nth: starts from 1
     * @return null if no N-th
     */
    fun inOrderNth(nth: Int, reverse:Boolean=false): T? {
        val limit = size
        if (nth > limit)
            return null
        var progress = 0
        class _InterException(val key: T) :Exception()
        fun _inTraverse(node: BinaryNode<T>?){
            if (node == null)
                return
            else {
                _inTraverse(node.left)
                if(++progress >= nth)
                    throw _InterException(node.key)
                _inTraverse(node.right)
            }
        }
        fun _reverseInTraverse(node: BinaryNode<T>?){
            if (node == null)
                return
            else {
                _reverseInTraverse(node.right)
                if(++progress >= nth)
                    throw _InterException(node.key)
                _reverseInTraverse(node.left)
            }
        }
        try {
            if (reverse)
                _reverseInTraverse(rootNode)
            else
                _inTraverse(rootNode)
        }
        catch (e: _InterException){
            return e.key
        }
        return null
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
        _inOrderTraverse(rootNode, "", callback)
    }




    /**
     * Delete a binaryNode following the procedure written in Wikipedia
     * @param key
     * @return success => true
     * @throws DeleteFailException
     */
    override fun delete(key: T): DeleteResult {
        //class ImproperArgumentException(msg:String) : Exception(msg)
        tailrec fun _delete_node(self: BinaryNode<T>?, parent: BinaryNode<T>?){//: DeleteResult { //Pair<T, T>?{
            // Delete self binaryNode
            fun __delete_self_node(self: BinaryNode<T>, parent: BinaryNode<T>?){
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
            fun __replace(self: BinaryNode<T>, child: BinaryNode<T>, parent: BinaryNode<T>?){
                if (parent == null) {
                    rootNode = child
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
                throw DeleteFailException()//return DeleteResult.NO_MATCH
            else {
                if (key < self.key) {
                    return _delete_node(self.left, self)
                } else if (key > self.key) {
                    return _delete_node(self.right, self)
                } else { // key == self.key
                    // 3. delete self if no iterCount
                    val bL = if(self.left == null) 0 else 1
                    val bR = if(self.right == null) 0 else 2
                    when(bL or bR){
                        0 -> {
                            __delete_self_node(self, parent)
                            throw DeleteSuccessException(DeleteResult.SELF_DELETE)
                        }
                        1 -> {
                            __replace(self, self.left!!, parent)
                            throw DeleteSuccessException(DeleteResult.LEFT_REPLACE)
                        }
                        2 -> {
                            __replace(self, self.right!!, parent)
                            throw DeleteSuccessException(DeleteResult.RIGHT_REPLACE)
                        }
                        else -> {
                            __replace2(self)
                            throw DeleteSuccessException(DeleteResult.PREDEC_REPLACE)
                        }
                    }
                }
            }
        }
        var result = DeleteResult.EMPTY
        if (rootNode != null) {
            try {
                _delete_node(rootNode, null)
            } catch(dE: DeleteException) {
                if (dE is DeleteSuccessException) {
                    //if (deleted != DeleteResult.NO_MATCH){
                    --_size
                    result = dE.result
                } else
                    throw dE // result = DeleteResult.NO_MATCH
            }
        }
        return result
    }

    /**
     * -= operator
     */
    operator fun minusAssign(k: T){ delete(k)}

    /**
     * Iterable
     */
    override operator fun iterator(): Iterator<T>{
        return NodeIterator2()
    }

    /**
     * NodeIterator by list
     */
    inner class NodeIterator1: Iterator<T> {
        val list = mutableListOf<T>()
        var n = 0

        init {
            inTraverse { k ->
                list.add(k)
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
    /**
     * NodeIterator using nthKey
     */
    inner class NodeIterator2(val reverse: Boolean=false): Iterator<T> {
        var progress = 0

        override fun hasNext(): Boolean {
            return progress < size
        }

        override fun next(): T {
            val nthKey = inOrderNth(++progress, reverse)
            if (nthKey != null)
                return nthKey
            else
                throw NoSuchElementException()
        }
    }
}


