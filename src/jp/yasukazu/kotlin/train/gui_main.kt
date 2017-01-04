package jp.yasukazu.kotlin.train

import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.*
import javax.swing.event.TreeModelListener
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
//import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

fun List<String>.join(d:String):String{
    val sb = StringBuilder()
    this.forEach { s ->
        sb.append("$s$d")
    }
    return sb.toString()
}
/**
 * Custom JTree Model
 */
open class BinarySearchTreeModel<T:Comparable<T>>(_root: BinaryNode<T>? = null) : BinarySearchTree<T>(), TreeModel {
    val listenerList = mutableListOf<TreeModelListener>()
    init {
        if (_root != null) {
            rootNode = _root
            //forceSetSize(_root.count)
        }

    }
    override fun getRoot(): BinaryNode<T>? { return rootNode
    }
    override fun isLeaf(_n: Any): Boolean {
        val n = _n as BinaryNode<T>
        return n.left == null && n.right == null
    }
    override fun getChildCount(_n: Any): Int {
        val n = _n as BinaryNode<T>
       return n.size //j(if(n.left != null) 1 else 0) + (if(n.right != null) 1 else 0)
    }

    override fun removeTreeModelListener(l: TreeModelListener?) {
        if(l != null)
            listenerList.remove(l)//TreeModelListener::class.java, l)
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIndexOfChild(parent: Any?, child: Any?): Int {
        if (parent == null && child == null)
            return -1
        else {
            val p = parent as BinaryNode<T>
            when(p.childrenStatus){
                0 -> return -1
                1 -> return 0
                2 -> return 1
                else -> {
                    val c = child as BinaryNode<T>
                    return if (c == p.left) 0 else 1
                }
            }
        }
    }

    override fun getChild(parent: Any?, index: Int): Any? {
        if (parent == null)
            return null//BinaryNode<T>(null)
        else {
            val p = parent as BinaryNode<T>
            return when(p.childrenStatus){
                0 -> null
                1 -> p[0]
                2 -> p[1]
                else -> p[index]
            }
        }
    }

    override fun addTreeModelListener(l: TreeModelListener?) {
        if (l != null)
            listenerList.add(l)//TreeModelListener::class.java, l)
    }

    fun clone(): BinarySearchTreeModel<T>{
        val tree = copy()
        val newModel = BinarySearchTreeModel(tree?.rootNode)
        return newModel
    }

}

//enum class Insert_Delete {INSERT, DELETE}
open class BinarySearchTreeFrame<T : Comparable<T>>(val model: BinarySearchTreeModel<T>, val fromString:(String)->T?) : JFrame() {
    val inputArea = JTextArea()
    val insertButton = JButton("Insert")
    val deleteButton = JButton("Delete")
    val statusLabels = arrayOf(JLabel(), JLabel(), JLabel(), JLabel(), JLabel(), JLabel())
    //enum class StatusLabelEnum(val value: Int) { KEY(0), LEFT(1), RIGHT(2), SIZE(3), STATUS(4) }
    val panel = JPanel()
    val subPanel = JPanel()
    val statusPanel = JPanel()
    var tree: JTree? = null
    val treeSizeLabel = JLabel()
    val cloneButton = JButton("Clone self")
    val treeTopLabel = JLabel()
    val treeLastLabel = JLabel()
    val existsListModel = DefaultListModel<String>()
    val existsList = JList<String>(existsListModel)//JLabel()
    val entryPanel = JPanel()
    inner class OriginalTreeSelectionListener : TreeSelectionListener{
        override fun valueChanged(e: TreeSelectionEvent?) {
            val last = tree?.lastSelectedPathComponent
            if (last != null) {
                @Suppress("UNCHECKED_CAST")
                val node = last as BinaryNode<T>
                //existsListModel.clear()
                inputArea.text = "${node.key}"
                statusLabels[0].text = "${node.key}"
                statusLabels[1].text = "${node.left?.key}"
                statusLabels[2].text = "${node.right?.key}"
                statusLabels[3].text = "${node.size}"
                statusLabels[4].text = "${node.childrenStatus}"
                val path = model.findPath(node.key)
                if (path != null) {
                    val sb = StringBuilder()
                    path.forEach { it ->
                        sb.append("${it.key}, ")
                    }
                    statusLabels[5].text = "$sb"
                }
            }
        }
    }
    val originalTreeSelectionListener = OriginalTreeSelectionListener()
    //val entryLabels = arrayOf(JLabel(), JLabel())


    fun getTopN(i:Int, reverse:Boolean=false):String {
        var n = i
        val keyList = mutableListOf<String>()
        model.inTraverse(reverse=reverse){ k ->
            keyList.add("$k")
            return@inTraverse --n > 0
        }
        return keyList.join(" ")
    }

    init {
        tree = JTree(model)
        tree?.addTreeSelectionListener(originalTreeSelectionListener)
        with(subPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JScrollPane(tree))
        }
        with(statusPanel) {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            val leftColumn = JPanel()
            leftColumn.layout = BoxLayout(leftColumn, BoxLayout.Y_AXIS)
            val statusTitles = arrayOf("key", "left", "right", "size", "childrenStatus", "path")
            statusTitles.forEach { title ->
                val label = JLabel("$title:")
                label.alignmentX = 1.0f
                leftColumn.add(label)
            }
            val rightColumn = JPanel()
            rightColumn.layout = BoxLayout(rightColumn, BoxLayout.Y_AXIS)
            statusLabels.forEach {it ->
                rightColumn.add(it)
            }
            add(leftColumn)
            add(rightColumn)
        }

        fun setTreeStatus(n: Int = 10){
            treeSizeLabel.text = "Tree Size: ${model.size}"
            treeTopLabel.text = "Sorted $n Tops: ${getTopN(n)}"
            treeLastLabel.text = "Sorted $n Lasts: ${getTopN(n, reverse = true)}"
        }


        /**
         * @param method: like {i->model.insert(i)}
         */
        class InsertActionListener2(val method: (T)->Any): ActionListener {
            override fun actionPerformed(e: ActionEvent?) {
                existsListModel.clear()
                //val listModel = DefaultListModel<String>()
                inputArea.text.split('\n').forEach { s ->
                    val i: T? = fromString(s.trim())
                    if (i != null) {
                        val result = method(i)
                        existsListModel.addElement("$i->$result")
                    }
                }
                subPanel.removeAll()
                tree = JTree(model)
                tree?.addTreeSelectionListener(originalTreeSelectionListener)
                subPanel.add(JScrollPane(tree))
                //existsList.model = listModel
                subPanel.revalidate()
                setTreeStatus()
                //existsList.text = sb.toString()
            }
        }
        insertButton.addActionListener(InsertActionListener2{i->model.insert(i)})//(Insert_Delete.INSERT))

        deleteButton.addActionListener(InsertActionListener2{i->model.delete(i)})//InsertActionListener(Insert_Delete.DELETE))

        val existsButton = JButton("Check existence")
        existsButton.addActionListener { e ->
            //val listModel = DefaultListModel<String>()
            existsListModel.clear()
            inputArea.text.split("\n").forEach { s ->
                val i: T? = fromString(s.trim())
                if (i != null){// && model != null){
                    val sb = StringBuilder()
                    val path = model.findPath(i)
                   if (path != null) {//i in model)
                       //val sb = StringBuilder()
                       path.forEach { it ->
                           sb.append("${it.key}, ")
                       }
                       existsListModel.addElement(sb.toString())
                   }
                    else
                       existsListModel.addElement("$i does not exist.")
                }
                //existsList.model = listModel
                existsList.revalidate()
            }
        }
        cloneButton.addActionListener { e ->
            val cloneModel = model.clone()
            SwingUtilities.invokeLater {
                val newFrame = BinarySearchTreeFrame(cloneModel, fromString)
                newFrame.title = "Clone of ${newFrame.title}"
                newFrame.pack()
                newFrame.isVisible = true
            }
        }
        val existsPanel = JPanel()
        with(existsPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(existsButton)
            add(JScrollPane(existsList))
        }
        val westPanel = JPanel()
        with(westPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(insertButton)
            add(existsPanel)
            add(cloneButton)
        }
        treeSizeLabel.text = "Tree Size: ${model.size}"
        treeTopLabel.text = "Sorted 3 Tops: ${getTopN(3)}"
        treeLastLabel.text = "Sorted 3 Lasts: ${getTopN(3, reverse = true)}"

        with(entryPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Input below(multi-line OK:"))
            add(inputArea)
            add(JLabel("Tree status:"))
            add(treeSizeLabel)
            add(treeTopLabel)
            add(treeLastLabel)
        }
        with(panel) {
            val _layout = BorderLayout()//this, BoxLayout.Y_AXIS)
            _layout.hgap = 20
            _layout.vgap = 20
            layout = _layout
            add(entryPanel, BorderLayout.NORTH)
            add(westPanel, BorderLayout.WEST)
            add(subPanel, BorderLayout.CENTER)
            add(deleteButton, BorderLayout.EAST)
            add(statusPanel, BorderLayout.SOUTH)
        }
        setTreeStatus()
        add(panel)
        title = "Integer Binary Search Tree"
    }
}

/**
 * GUI main test
 * Created by Yasukazu on 2016/12/06.
 */
fun main(args:Array<String>){
    val btree = BinarySearchTree<Int>()
    print("\n")
    val argString = "5,2,12,8, 10,9, 7,21,19,25"
    val keys = argString.split(",") //replace("""[ \subPanel]""", "").) //arrayOf( "B", "B", "A", "A", "C", "B1", "A", "B2", "A2", "A1")
    keys.forEach { s ->
        val x = s.trim()
        val i : Int? =
                try {
                    x.toInt()
                }
                catch(e: IllegalFormatException){
                    null
                }
        if (i != null){
            val inserted_pos = btree.insert(i)
            val pos = when(inserted_pos) {
                InsertedPos.LEFT -> "Left"
                InsertedPos.RIGHT -> "Right"
                InsertedPos.NONE -> "None"
                InsertedPos.NEW -> "New"
                else -> "??"
            }
            println("$x insert result: $pos")
        }
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
    btree.preTraverse_depth("") {s,d->println("$s:$d")}
    println()
    println("pre-order traversal with LR")
    btree.preTraverseLR {k,lr -> println("$lr->$k")}
    println()
    println("post order traversal with depth")
    btree.postTraverse_depth {s,d->println("$s:$d")}
    println("pre-order traversal with print")
    btree.preTraversePrint {s -> print("$s")}
    println()
    // node copy
    val newRoot = btree.rootNode?.copy()
    if (newRoot != null){
        val newBtree = BinarySearchTree<Int>()
        newBtree.rootNode = newRoot
        println("Cloned binary search tree:")
        newBtree.preTraversePrint { s -> print("$s") }
        println()
    }

    println("\nBTree in-order traversal:")
    btree.inTraverse { x ->
        println(x)
        return@inTraverse true
    }
    println()
    print("\nBTree reverse in-order traversal:\n")
    btree.inTraverse(reverse = true, callback={ x ->
        println(x)
        return@inTraverse true
    })
    println()
    println("\nBTree interrupt in-order traversal:")
    var count = 2
    btree.inTraverse(reverse = true) { x ->
        // writeInt(x)
        println(x)
        return@inTraverse --count > 0 //if(count <= 0) true else false
    }
    println()
    // try catch version
    class InterruptException: Exception()
    count = 2
    try {
        btree.inTraverse { x ->
            println(x)
            if(--count <= 0)
                throw InterruptException()
            return@inTraverse true
        }
    }
    catch(e: InterruptException){}
    println()
    print("\nBTree in-order traversal with depth:\n")
    btree.inOrderTraverse {s,d->println("$s:$d")}
    print("\n")
    val find_keys = arrayOf(5, 4, 3)//"A", "B", "B1", "B2", "C")
    find_keys.forEach { x ->
        //println(if(btree.find(x)) "$x exists" else "$x does not exist")
        println(if(x in btree) "$x exists." else "$x does not exist.")
    }
    println()
    println("BTree in-order n-ths")
    for (n in 1..btree.size){
        val iN2 = btree.inOrderNth(n)
        println("BTree in-order n-th($n)=$iN2")
    }
    println()

    /* Iterate 1 */
    println("Iterator by list")
    btree.NodeIterator1().forEach { it -> print("$it, ") }
    println()
    /* Iterate 2 */
    println("Iterator by inOrderNth")
    val it2 = btree.NodeIterator2()
    it2.forEach { it -> print("$it, ") }
    println()
    /* Iterate */
    println("Iteration of Tree:")
    btree.forEach { it ->
        print("$it, ")
    }
    println()

    val treeModel = BinarySearchTreeModel<Int>()
    btree.preTraverse { k ->
        treeModel.insert(k)
    }


    fun stringToInt(a:String):Int?{
         return try { a.toInt() } catch (e: NumberFormatException){null}
    }

    SwingUtilities.invokeLater {
        val treeFrame = BinarySearchTreeFrame(treeModel, ::stringToInt)
        with(treeFrame){
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            pack()
            isVisible = true
        }
        /*
        val frame = JFrame()//"Binary Search Tree")
        //frame.contentPane = binaryTreeForm.panel1
        frame.contentPane.layout = BorderLayout()
        frame.contentPane.add(initialPanel, BorderLayout.NORTH)
        frame.contentPane.add(treePanel, BorderLayout.CENTER)
        frame.contentPane.add(msgPanel, BorderLayout.SOUTH)
        frame.contentPane.add(btnPanel, BorderLayout.EAST)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
        frame.isVisible = true
        */
    }
}
