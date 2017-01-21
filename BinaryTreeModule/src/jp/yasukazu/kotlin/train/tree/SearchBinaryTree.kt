package jp.yasukazu.kotlin.train.tree
import jp.yasukazu.kotlin.train.tree.SearchBinaryNodeInterface
/**
 * Created by Yasukazu on 2017/01/20.
 *
 */
enum class DeleteResult {SELF_DELETE, LEFT_REPLACE, RIGHT_REPLACE, PREDEC_REPLACE, NO_MATCH, EMPTY}

open class InsertDeleteException: Exception()
open class DeleteException: InsertDeleteException()
class DeleteFailException: DeleteException()
class DeleteSuccessException(val result: DeleteResult): DeleteException()

interface SearchBinaryTreeInterface<T: Comparable<T>> {
    val size: Int
    operator fun contains(item: T): Boolean
    val root: SearchBinaryNodeInterface<T>?
    fun add(item: T)
    fun remove(item: T, callback: ((DeleteResult)->Unit)?=null)
    fun preTraverse(callback: (T)->Unit)
    fun inTraverse(callback: (T)->Unit)
    fun postTraverse(callback: (T)->Unit)
    fun rootNodeCopy(): SearchBinaryNode<T>?
    fun nodeInterface(item: T): SearchBinaryNodeInterface<T>?
    fun nodeCopy(item: T): SearchBinaryNode<T>?
}

class SearchBinaryTree<T: Comparable<T>>(private var rootNode: SearchBinaryNodeInterface<T>?=null) : SearchBinaryTreeInterface<T> {
    //var rootNode = node
    ///override val root: SearchBinaryNode<T>? get() { return rootNode }
    override val size: Int get() {return count()}

    override fun contains(item: T): Boolean {
        if (rootNode != null)
            return item in rootNode!!
        return false
    }
    override val root: SearchBinaryNodeInterface<T>? = rootNodeInterface()
    //override fun remove(item: T) { throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates. }
    /**
     * Delete a binaryNode following the procedure written in Wikipedia
     * @param key
     * @return success => true
     * @throws DeleteFailException
     */
    override fun remove(item: T, callback : ((DeleteResult) -> Unit)?){
        tailrec fun _delete_node(self: SearchBinaryNodeInterface<T>?, parent: SearchBinaryNodeInterface<T>?){
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
            fun __replace(self: SearchBinaryNodeInterface<T>, child: SearchBinaryNode<T>, parent: SearchBinaryNodeInterface<T>?){
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
            fun __replace2(self: SearchBinaryNode<T>){
                assert(self.left != null && self.right != null)
                fun getPredecessorNode(_self: SearchBinaryNode<T>, _parent: SearchBinaryNode<T>) : Pair<SearchBinaryNode<T>, SearchBinaryNode<T>> {
                    var s = _self
                    var p = _parent
                    while (s.right != null) {
                        p = s // Reserve the parent first
                        s = s.right!! as SearchBinaryNode<T>
                    }
                    return Pair(s, p)
                }
                val (predNode, predParent) = getPredecessorNode(self.left!! as SearchBinaryNode<T>, self) // in-order predecessor
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
                    return _delete_node(self.left, self)
                } else if (item > self.key) {
                    return _delete_node(self.right, self)
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
                            __replace(self, self.left!! as SearchBinaryNode<T>, parent)
                            throw DeleteSuccessException(DeleteResult.LEFT_REPLACE)
                        }
                        2 -> {
                            __replace(self, self.right!! as SearchBinaryNode<T>, parent)
                            throw DeleteSuccessException(DeleteResult.RIGHT_REPLACE)
                        }
                        else -> {
                            __replace2(self as SearchBinaryNode<T>)
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
                    //--_size
                    result = dE.result
                } else
                    throw dE // result = DeleteResult.NO_MATCH
            }
        }
        if (callback != null)
            callback(result)
    }

    override fun add(item: T){
        if (rootNode != null)
            rootNode!!.add(item)
        else
            rootNode = SearchBinaryNode(item)
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

    override fun rootNodeCopy(): SearchBinaryNode<T>?{
        if (rootNode != null)
            return (rootNode as SearchBinaryNode<T>).copy()
        else
            return null
    }

    override fun nodeInterface(item: T): SearchBinaryNodeInterface<T>? = rootNode?.findNode(item)

    override fun nodeCopy(item: T): SearchBinaryNode<T>? {
        val result = rootNode?.findNode(item)
        if (result != null)
            return (result as SearchBinaryNode<T>).copy()
        return null
    }
}

fun main(args: Array<String>){
    /*
    val nodeData1 = BinaryNodeData(3)
    val nodeData2 = BinaryNodeData(1)
    nodeData1.left = nodeData2
    val nodeData3 = nodeData1.copy()
    println(
            """$nodeData1:hash=${"%X".format(nodeData1.hashCode())},
            |$nodeData2,
            |$nodeData3""")
            */
    val item1 = 5
    val node1 = SearchBinaryNode(item1)
    println("As parameter=$item1, SearchBinaryNode is generated: $node1")
    println(if (item1 in node1) "node1 contains $item1." else "node1 does not contains $item1.")
    val newKey = 3
    val retval = node1.add(newKey)
    println("Added $newKey: returned $retval")
    println("$node1")
    val newKey2 = 4
    node1.add(newKey2)
    val node2 = node1.copy()
    println("A copy of SearchBinaryNode is generated.")
    val newKey3 = 7
    node2.add(newKey3)
    println("New key $newKey3 is added to Node 2")
    println("Node 1(count=${node1.total}): $node1")
    println("Node 2(count=${node2.total}): $node2")
    println("Node 1 traverse")
    val node1count = node1.traverse { println(it) }
    println("Count = $node1count")
    val tree: SearchBinaryTreeInterface<Int> = SearchBinaryTree()
    tree.add(7)
    tree.add(9)
    tree.add(5)
    tree.add(10)
    tree.add(4)
    val treeRoot = tree.root//NodeInterface()//tree as SearchBinaryTree).rootNode
    println("Tree root is $treeRoot")
    val min = tree.root?.min
    val max = tree.root?.max
    println("Min = $min, Max = $max")

    val size = tree.size
    println("Tree size = $size")
    println("Pre-order Traversal:")
    tree.preTraverse(::println)
    println()
    println("In-order Traversal:")
    tree.inTraverse(::println)
    println()
    println("Post-order Traversal:")
    tree.postTraverse(::println)
    println("Make a copy of tree 1:")
    val tree_root_copy = tree.rootNodeCopy()
    println("Tree root node copy: $tree_root_copy")
    val tree2: SearchBinaryTreeInterface<Int> = SearchBinaryTree(tree_root_copy)
    tree2.inTraverse ( ::println )
    println()
    val add = 1
    tree2.add(add)
    println("$add is added to tree 2")
    tree2.inTraverse (::println)
}

