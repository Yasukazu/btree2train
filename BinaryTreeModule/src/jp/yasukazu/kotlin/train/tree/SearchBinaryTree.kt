package jp.yasukazu.kotlin.train.tree
/**
 * Created by Yasukazu on 2017/01/20.
 *
 */

class SearchBinaryTree<T: Comparable<T>>(private var rootNode: SearchBinaryNodeInterface<T>?=null) : SearchBinaryTreeInterface<T> {
    //var rootNode = node
    ///override val root: BasicSearchBinaryNode<T>? get() { return rootNode }
    override val size: Int get() {return count()}

    override fun contains(item: T): Boolean {
        if (rootNode != null)
            return item in rootNode!!
        return false
    }
    private var _root: BasicSearchBinaryNode<T>? = null
    override val root: SearchBinaryNodeInterface<T>? = _root // rootNodeInterface()
    //override fun remove(item: T) { throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates. }
    /**
     * Delete a binaryNode following the procedure written in Wikipedia
     * @param key
     * @return success => true
     * @throws DeleteFailException
     */
    tailrec fun _delete_node(item: T, self: SearchBinaryNodeInterface<T>?, parent: SearchBinaryNodeInterface<T>?){
        // Delete self binaryNode
        fun __delete_self_node(self: SearchBinaryNodeInterface<T>, parent: SearchBinaryNodeInterface<T>?){
            assert(self.left == null && self.right == null)
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
        // replace self with 1 child
        fun __replace(self: SearchBinaryNodeInterface<T>, child: SearchBinaryNodeInterface<T>, parent: SearchBinaryNodeInterface<T>?){
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
        fun __replace2(self: SearchBinaryNodeInterface<T>){
            assert(self.left != null && self.right != null)
            fun getPredecessorNode(_self: SearchBinaryNodeInterface<T>, _parent: SearchBinaryNodeInterface<T>) : Pair<SearchBinaryNodeInterface<T>, SearchBinaryNodeInterface<T>> {
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
        }
        // code starts here
        if (self == null)
            throw DeleteFailException()//return DeleteResult.NO_MATCH
        else {
            if (item < self.key) {
                return _delete_node(self.left!!.key, self, parent)
            } else if (item > self.key) {
                return _delete_node(self.right!!.key, self, parent)
            } else { // item == self.item
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
    override fun remove(item: T, callback : ((DeleteResult) -> Unit)?){
        var result = DeleteResult.EMPTY
        if (rootNode != null) {
            try {
                _delete_node(item, rootNode, null)
            }
            catch(dE: DeleteException){
                if (dE is DeleteSuccessException) {
                    result = dE.result
                } else
                    throw dE
            }
        }
        if (callback != null)
            callback(result)
    }

    override fun add(item: T){
        if (rootNode != null)
            rootNode!!.add(item)
        else
            rootNode = BasicSearchBinaryNode(item)
    }

    fun count(): Int{
        var n = 0
        fun _traverse(node: SearchBinaryNodeInterface<T>?){
            if (node == null)
                return
            else {
                ++n
                _traverse(node.left)
                _traverse(node.right)
            }
        }
        _traverse(rootNode)
        return n
    }

    override fun preTraverse(callback: (T) -> Unit){
        fun _traverse(node: SearchBinaryNodeInterface<T>?){
            if (node == null)
                return
            else {
                callback(node.key)
                _traverse(node.left)
                _traverse(node.right)
            }
        }
        _traverse(rootNode)
    }
    override fun inTraverse(callback: (T) -> Unit){
        fun _inTraverse(node: SearchBinaryNodeInterface<T>?){
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
    override fun postTraverse(callback: (T) -> Unit){
        fun _traverse(node: SearchBinaryNodeInterface<T>?){
            if (node == null)
                return
            else {
                _traverse(node.left)
                _traverse(node.right)
                callback(node.key)
            }
        }
        _traverse(rootNode)
    }

    fun rootNodeInterface(): SearchBinaryNodeInterface<T>? {
        return rootNode
    }

    override fun rootNodeCopy(): SearchBinaryNodeInterface<T>?{
        if (rootNode != null)
            return (rootNode as BasicSearchBinaryNode<T>).copy()
        else
            return null
    }

    override fun nodeInterface(item: T): SearchBinaryNodeInterface<T>? = rootNode?.findNode(item)?.second

    override fun nodeCopy(item: T): SearchBinaryNodeInterface<T>? {
        val result = rootNode?.findNode(item)
        if (result != null)
            if (result.first)
                return (result.second as BasicSearchBinaryNode<T>).copy()
        return null
    }
}



