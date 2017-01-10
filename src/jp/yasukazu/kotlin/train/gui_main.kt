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
        @Suppress("UNCHECKED_CAST")
            val n = _n as BinaryNode<T>
            return n.left == null && n.right == null
    }
    override fun getChildCount(_n: Any): Int {
        @Suppress("UNCHECKED_CAST")
        val n = _n as BinaryNode<T>
       return n.size //j(if(n.left != null) 1 else 0) + (if(n.right != null) 1 else 0)
    }

    override fun removeTreeModelListener(l: TreeModelListener?) {
        if(l != null)
            listenerList.remove(l)//TreeModelListener::class.java, l)
    }

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
        println("valueForPathChanged called: path=$path, newValue=$newValue")
    }

    override fun getIndexOfChild(parent: Any?, child: Any?): Int {
        if (parent == null && child == null)
            return -1
        else {
            @Suppress("UNCHECKED_CAST")
            val p = parent as BinaryNode<T>
            when(p.childrenStatus){
                0 -> return -1
                1 -> return 0
                2 -> return 1
                else -> {
                    @Suppress("UNCHECKED_CAST")
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
            @Suppress("UNCHECKED_CAST")
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
open class BinarySearchTreeFrame<T: Comparable<T>>(val model: BinarySearchTreeModel<T>, val fromString: (String)->T) : JFrame() {
    val inputArea = JTextArea()
    val insertButton = JButton("Insert")
    val deleteButton = JButton("Delete")
    val statusLabels = arrayOf(JLabel(), JLabel(), JLabel(), JLabel(), JLabel(), JLabel())
    //enum class StatusLabelEnum(val value: Int) { KEY(0), LEFT(1), RIGHT(2), SIZE(3), STATUS(4) }
    val panel = JPanel()
    val subPanel = JPanel()
    val statusPanel = JPanel()
    var tree = JTree(model) // ? = null
    val treeSizeLabel = JLabel()
    val cloneButton = JButton("Clone self")
    val treeTopLabel = JLabel()
    val treeLastLabel = JLabel()
    val existsListModel = DefaultListModel<String>()
    val existsList = JList<String>(existsListModel)//JLabel()
    val entryPanel = JPanel()
    val controlPanel = JPanel()
    val outputPanel = JPanel()
    val outputArea = JTextArea()

    inner class OriginalTreeSelectionListener : TreeSelectionListener{
        override fun valueChanged(e: TreeSelectionEvent?) {
            val last = tree.lastSelectedPathComponent
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

    /**
     * secondary constructor
    constructor(val fromString:(String)->T) : this(model, fromString){
        this(BinarySearchTreeModel<T>, fromString)
    }
     */

    fun getTopN(i:Int, reverse:Boolean=false):String {
        var n = i
        val keyList = mutableListOf<String>()
        class _BreakException: Exception()
        val keyListAdd = fun(it: T) {
            keyList.add("$it")
            if (--n <= 0)
                throw _BreakException()
        }
        try {
            when (reverse) {
                false -> model.inTraverse { keyListAdd(it) }
                else -> model.reverseInTraverse(keyListAdd)
            }
        } catch (e: _BreakException) { }
        return keyList.join(" ")
    }

    fun setTreeStatus(n: Int = 10){
        treeSizeLabel.text = "Tree Size: ${model.size}"
        treeTopLabel.text = "Sorted $n Tops: ${getTopN(n)}"
        treeLastLabel.text = "Sorted $n Lasts: ${getTopN(n, reverse = true)}"
    }

    init {
        //tree = JTree(model)
        tree.addTreeSelectionListener(originalTreeSelectionListener)
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



        /**
         * @param method: like {i->model.insert(i)}
         */
        class InsertActionListener2(val method: (T)->Any) : ActionListener {
            override fun actionPerformed(actionEvent: ActionEvent?) {
                existsListModel.clear()
                val ss = inputArea.text.split('\n')
                ss.forEach {
                    val s = it.trim()
                    try {
                        val k = fromString(s)
                        val result = method(k)
                        existsListModel.addElement("$k: $result")
                    }
                    /*
                    catch (idE: BinarySearchTree.InsertDeleteException){
                        when(idE) {
                            BinarySearchTree.InsertException -> existsListModel.addElement("$s caused an exception: $actionEvent")
                        }
                    }*/
                    catch (idE: BinarySearchTree.InsertDeleteException){
                        if (idE is BinarySearchTree.InsertFailException)
                            existsListModel.addElement("$s caused an InsertFailException")
                        else
                            existsListModel.addElement("$s caused a DeleteFailException")
                    }
                        catch(e: Exception){
                            existsListModel.addElement("$s caused an exception: $e")
                        }
                }
                /*
                tree.revalidate()
                */
                subPanel.removeAll()
                tree = JTree(model)
                tree.addTreeSelectionListener(originalTreeSelectionListener)
                subPanel.add(JScrollPane(tree))
                //tree.collapseRow(0)
                tree.expandRow(0)
                //existsList.model = listModel
                tree.revalidate()
                setTreeStatus()
                //existsList.text = sb.toString()
            }
        }
        insertButton.addActionListener(InsertActionListener2 { model.insert(it) })//(Insert_Delete.INSERT))

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
        val preTraverseButton = JButton("Pre-order traversal")
        preTraverseButton.addActionListener {
            val sb = StringBuilder()
            model.preTraverse { sb.append("$it\n") }
            outputArea.text = sb.toString()
            outputPanel.revalidate()
        }
        val preTraverseDepthButton = JButton("Pre-order traversal with depth")
        preTraverseDepthButton.addActionListener {
            val sb = StringBuilder()
            model.preTraverse_depth_LR { t, i, c ->
                var n =i
                while(n-- > 0)
                    sb.append(c)
                sb.append(" $t\n")
            }
            outputArea.text = sb.toString()
            outputPanel.revalidate()
        }
        with(controlPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(existsPanel)
            add(Box.createGlue())
            add(insertButton)
            add(Box.createGlue())
            add(cloneButton)
            add(Box.createGlue())
            add(deleteButton)
            add(Box.createGlue())
            add(preTraverseButton)
            add(Box.createGlue())
            add(preTraverseDepthButton)
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
            add(Box.createGlue())
            add(statusPanel)
        }
        with(outputPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JScrollPane(outputArea))
        }
        with(panel) {
            val _layout = BorderLayout()//this, BoxLayout.Y_AXIS)
            _layout.hgap = 20
            _layout.vgap = 20
            layout = _layout
            add(entryPanel, BorderLayout.NORTH)
            add(controlPanel, BorderLayout.WEST)
            add(subPanel, BorderLayout.CENTER)
            add(outputPanel, BorderLayout.EAST)
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
    println("pre-order traversal:")
    btree.preTraverse {print("$it\n") }
    println()
    println("pre-order traversal 1st 3 break: ")
    class _BreakException : Exception()
    var n = 3
    try {
        btree.preTraverse { print("$it, ") }
        if (--n <= 0)
            throw _BreakException()
    } catch (e: _BreakException) {
    }
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
    btree.preTraversePrint {print("$it, ")}
    println()
    // node copy
    val newRoot = btree.rootNode?.copy()
    if (newRoot != null){
        val newBtree = BinarySearchTree<Int>()
        newBtree.rootNode = newRoot
        println("Cloned binary search tree:")
        newBtree.preTraversePrint {print("$it, ") }
        println()
    }

    println("BTree in-order traversal:")
    btree.inTraverse { println(it) }
    println()
    print("BTree reverse in-order traversal:\n")
    btree.reverseInTraverse { println(it) }
    println()
    println("BTree interrupt in-order traversal:")
    class _InterruptException: Exception()
    var count = 2
    try {
        btree.inTraverse {
            println(it)
            if (--count <= 0)
                throw _InterruptException()
        }
    } catch(e: _InterruptException){}
    println()
        /*
    // try catch version
    class InterruptException: Exception()
    count = 2
    try {
        btree.inTraverse { x ->
            println(x)
            if(--count <= 0)
                throw InterruptException()
        }
    }
    catch(e: InterruptException){}
    println() */
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
    for (_n in 1..btree.size){
        val iN2 = btree.inOrderNth(_n)
        println("BTree in-order n-th($_n)=$iN2")
    }
    println()

    /* Iterate 1 */
    println("Iterator by list")
    btree.NodeIterator1().forEach { it -> print("$it, ") }
    println()
    /* Iterate 2 */
    println("Iterator by inOrderNth reverse")
    val it2 = btree.NodeIterator2(reverse=true)
    it2.forEach { it -> print("$it, ") }
    println()
    /* Iterate */
    println("Iteration of Tree:")
    btree.forEach { it ->
        print("$it, ")
    }
    println()

    val treeModel = BinarySearchTreeModel(btree.rootNode)
    /*
    btree.preTraverse { k ->
        treeModel.insert(k)
    }
    */

    // fun stringToInt(a:String):Int?{ return try { a.toInt() } catch (e: NumberFormatException){null} }

    val atoi = fun(a: String) = a.toInt()

    SwingUtilities.invokeLater {
        val treeFrame = BinarySearchTreeFrame(treeModel, atoi)
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
