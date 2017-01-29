package jp.yasukazu.kotlin.train.tree
/**
 * Created by Yasukazu on 2017/01/20.
 *
 */

class BasicSearchBinaryTree<T: Comparable<T>>(_root: SearchBinaryNode<T>?=null) : SearchBinaryTree<T> {
    //var rootNode = node
    ///override val root: BasicSearchBinaryNode<T>? get() { return rootNode }
    override val size: Int get() {return count()}

    override fun contains(item: T): Boolean {
        if (root != null)
            return item in root!!
        return false
    }
    //private var _root: BasicSearchBinaryNode<T>? = null
    override var root: SearchBinaryNode<T>? = _root // rootNodeInterface()
    //override fun remove(item: T) { throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates. }
    /**
     * Delete a binaryNode following the procedure written in Wikipedia
     * @param key
     * @return success => true
     * @throws DeleteFailException
     */

    override fun remove(item: T): DeleteResult {
        var result = DeleteResult.EMPTY
        if (root == null)
            return DeleteResult.EMPTY
        else {
            if (root!!.count() == 1){
                if (root!!.key < item || root!!.key > item)
                    return DeleteResult.NO_MATCH
                else {
                    root = null
                    return DeleteResult.SELF_DELETE
                }
            }
            else
            try {
                result = root!!.remove(item)
            }
            catch(dE: DeleteException){
                if (dE is DeleteSuccessException) {
                    result = dE.result
                } else
                    throw dE
            }
        }
        return result
    }


    override fun add(item: T){
        if (root != null)
            root!!.add(item)
        else
            root = BasicSearchBinaryNode(item)
    }

    fun count(): Int{
        var n = 0
        fun _traverse(node: SearchBinaryNode<T>?){
            if (node == null)
                return
            else {
                ++n
                _traverse(node.left)
                _traverse(node.right)
            }
        }
        _traverse(root)
        return n
    }

    override fun preTraverse(callback: (T) -> Unit){
        fun _traverse(node: SearchBinaryNode<T>?){
            if (node == null)
                return
            else {
                callback(node.key)
                _traverse(node.left)
                _traverse(node.right)
            }
        }
        _traverse(root)
    }
    override fun inTraverse(callback: (T) -> Unit){
        fun _inTraverse(node: SearchBinaryNode<T>?){
            if (node == null)
                return
            else {
                _inTraverse(node.left)
                callback(node.key)
                _inTraverse(node.right)
            }
        }
        _inTraverse(root)
    }
    override fun postTraverse(callback: (T) -> Unit){
        fun _traverse(node: SearchBinaryNode<T>?){
            if (node == null)
                return
            else {
                _traverse(node.left)
                _traverse(node.right)
                callback(node.key)
            }
        }
        _traverse(root)
    }

    fun rootNodeInterface(): SearchBinaryNode<T>? {
        return root
    }

    override fun rootNodeCopy(): SearchBinaryNode<T>?{
        if (root != null)
            return (root as BasicSearchBinaryNode<T>).copy()
        else
            return null
    }

    override fun nodeInterface(item: T): SearchBinaryNode<T>? = root?.findNode(item)?.second

    override fun nodeCopy(item: T): SearchBinaryNode<T>? {
        val result = root?.findNode(item)
        if (result != null)
            if (result.first)
                return (result.second as BasicSearchBinaryNode<T>).copy()
        return null
    }
}



