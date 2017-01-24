package jp.yasukazu.kotlin.train.tree

/**
 *
 * Created by Yasukazu on 2017/01/23.
 */
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
    val node1: SearchBinaryNodeInterface<Int> = BasicSearchBinaryNode(item1)
    println("As parameter=$item1, SearchBinaryNode is generated: $node1")
    println(if (item1 in node1) "node1 contains $item1." else "node1 does not contains $item1.")
    val newKey = 3
    var posNodeStr = ""
    node1.add(newKey) {node, pos, parent ->
        val nodeHex = "%X".format(node.hashCode())
        val parentHex = "%X".format(parent?.hashCode())
        posNodeStr = "$nodeHex at $pos under $parentHex" }
    println("Added $newKey : $posNodeStr")
    println("$node1 " + (if (newKey in node1) "contains" else "does not contains") + " $newKey")
    println("Traversal of node1:")
    node1.traverse { println(it) }
    val newKey2 = 4
    node1.add(newKey2) {node, pos, parent ->
        val nodeHex = "%X".format(node.hashCode())
        val parentHex = "%X".format(parent?.hashCode())
        posNodeStr = "$nodeHex at $pos under $parentHex" }
    println("Added $newKey2 : $posNodeStr")
    println("$node1 " + (if (newKey2 in node1) "contains" else "does not contains") + " $newKey2")
    println("Traversal of node1:")
    node1.traverse { println(it) }
    val node2: SearchBinaryNodeInterface<Int> = node1.copy()
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