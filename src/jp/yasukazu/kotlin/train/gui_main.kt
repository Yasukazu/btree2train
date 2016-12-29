package jp.yasukazu.kotlin.train

import java.awt.BorderLayout
import java.util.*
import javax.swing.*
import javax.swing.event.TreeModelListener
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath

/**
 * Custom JTree Model
 */
class BinarySearchTreeModel<T:Comparable<T>> : BinarySearchTree<T>(), TreeModel {
    val listenerList = mutableListOf<TreeModelListener>()
    override fun getRoot():Node<T>? { return rootNode }
    override fun isLeaf(_n: Any): Boolean {
        val n: Node<T> = _n as Node<T>
        return n.left == null && n.right == null
    }
    override fun getChildCount(_n: Any): Int {
        val n: Node<T> = _n as Node<T>
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
        if (parent == null)
            return -1
        else {
            val p = parent as Node<T>
            if (child == null)
                return -1
            else {
                val c = child as Node<T>
                when(p.childrenStatus){
                    0 -> return -1
                    1 -> return 0
                    2 -> return 1
                    else -> return if (c == p.left) 0 else 1
                }
            }
        }
    }

    override fun getChild(parent: Any?, index: Int): Any? {
        if (parent == null)
            return null//Node<T>(null)
        else {
            val p = parent as Node<T>
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

}


class IntBinarySearchTreeFrame(val model: BinarySearchTreeModel<Int>) : JFrame() {
    val inputField = JTextField()
    val insertButton = JButton("Insert")
    val deleteButton = JButton("Delete")
    val statusLabels = arrayOf(JLabel(), JLabel(), JLabel(), JLabel(), JLabel(), JLabel())
    enum class StatusLabelEnum(val value: Int) {
        KEY(0), LEFT(1), RIGHT(2), SIZE(3), STATUS(4)
    }
    val panel = JPanel()
    val subPanel = JPanel()
    val statusPanel = JPanel()
    var tree: JTree? = null

    inner class OriginalTreeSelectionListener : TreeSelectionListener{
        override fun valueChanged(e: TreeSelectionEvent?) {
            val last = tree?.lastSelectedPathComponent
            if (last != null) {
                val node = last as Node<Int>
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
    val entryLabels = arrayOf(JLabel(), JLabel())

    fun getTopN(i:Int): MutableList<String> {
        var n = i
        val keyList = mutableListOf<String>()
        val top3 = model.inTraverse(reverse=false){ k ->
            keyList.add("$k")
            return@inTraverse --n > 0
        }
        return keyList
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
        insertButton.addActionListener {
            val i:Int? = try {inputField.text.toInt() } catch (e: NumberFormatException) { null}
            if (i != null){
                model.insert(i)
                subPanel.removeAll()
                tree = JTree(model)
                tree?.addTreeSelectionListener(originalTreeSelectionListener)
                subPanel.add(JScrollPane(tree))
                subPanel.revalidate()
            }
        }
        deleteButton.addActionListener {
            val i:Int? = try {inputField.text.toInt() } catch (e: NumberFormatException) { null}
            if (i != null) {
                model -= i // deleteKey
                subPanel.removeAll()
                tree = JTree(model)
                tree?.addTreeSelectionListener(originalTreeSelectionListener)
                subPanel.add(JScrollPane(tree))
                subPanel.revalidate()
            }
        }
        val existsLabel = JLabel()
        val existsButton = JButton("Check existence")
        existsButton.addActionListener { e ->
                val i = try { inputField.text.toInt() } catch (e: NumberFormatException){null}
                if (i != null && model != null){
                    val path = model.findPath(i)
                   if (path != null) {//i in model)
                       val sb = StringBuilder()
                       path.forEach { it ->
                           sb.append("${it.key}, ")
                       }
                       existsLabel.text = "Exists. Path: $sb"
                   }
                    else
                       existsLabel.text = "Not exists"
                }
            }
        val existsPanel = JPanel()
        with(existsPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(existsButton)
            add(existsLabel)
        }
        val westPanel = JPanel()
        with(westPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(insertButton)
            add(existsPanel)
        }
        val entryPanel = JPanel()
        with(entryPanel){
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JLabel("Input below:"))
            add(inputField)
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
                BinarySearchTree.InsertedPos.LEFT -> "Left"
                BinarySearchTree.InsertedPos.RIGHT -> "Right"
                BinarySearchTree.InsertedPos.NONE -> "None"
                BinarySearchTree.InsertedPos.NEW -> "New"
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
    println("post order traversal with depth")
    btree.postTraverse_depth {s,d->println("$s:$d")}

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
    //val delete_value = 12 //"C"

    var root = btree.preTraverseTreeNode() //DefaultMutableTreeNode("root")
    val model = DefaultTreeModel(root)
    val msgLabel = JLabel()
    val msgLabel2 = JLabel()
    val tree = JTree(model)
    tree.addTreeSelectionListener { e ->
        val node = tree.lastSelectedPathComponent
        if (node != null){
            msgLabel.text = node.toString()
            val sb = StringBuilder()
            for (path in  e.paths)
                sb.append("$path,")
            msgLabel2.text = sb.toString()
        }
    }
    val treeModel = BinarySearchTreeModel<Int>()
    btree.preTraverse { k ->
        treeModel.insert(k)
    }
    val modelTree = JTree(treeModel)

    class TreeControlPanel<T:Comparable<T>>(tr: JTree) : JPanel() {
        //var node: BinarySearchTree.Node<T>? = null
        val textField = JTextField()
        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            add(JScrollPane(tr))
            add(textField)
            tr.addTreeSelectionListener { e ->
                val last = tr.lastSelectedPathComponent
                val node = last as Node<T>?
                textField.text = node?.key.toString()
            }
            val deleteButton = JButton("Delete")
            deleteButton.addActionListener { e ->
                val i = try { textField.text.toInt()} catch (e: IllegalFormatException) { null }
                if (i != null){
                    treeModel -= i //.deleteKey(i)
                }
            }
            add(deleteButton)
        }
    }
    val treeControlPanel = TreeControlPanel<Int>(modelTree)
    val treePanel = JPanel()
    with(treePanel){
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        add(JScrollPane(tree))
        add(treeControlPanel)//JScrollPane(modelTree))
    }
    val notifyLabel = JLabel("Blank leaf is left leaf.")
    for(i in 0 .. tree.rowCount-1)
        tree.expandRow(i)
    val delBtn = JButton("Delete item")//Keep data model delete")
    with(delBtn){
        addActionListener {
            val delete_value: Int? = try{ msgLabel.text.toInt() } catch(e: IllegalFormatException){ null }
            if(delete_value != null) {
                btree.deleteKey(delete_value)
                root = btree.preTraverseTreeNode()
                model.setRoot(root)
                for (i in 0..tree.rowCount - 1)
                    tree.expandRow(i)
            }
        }
    }
    val btnPanel = JPanel()
    with(btnPanel){
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        //add(showTreeButton)
        add(delBtn)
    }
    val msgPanel = JPanel()
    msgPanel.layout = BoxLayout(msgPanel, BoxLayout.PAGE_AXIS)
    msgPanel.add(msgLabel)
    msgPanel.add(msgLabel2)
    val initialPanel = JPanel()
    var newFrame: JFrame? = null
    with(initialPanel){
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(notifyLabel)
        val newFrameButton = JButton("Click to create a new frame")
        with(newFrameButton){
            addActionListener {
                SwingUtilities.invokeLater {
                    if (newFrame != null)
                        newFrame?.isVisible = false
                    newFrame = JFrame("New Frame")
                    newFrame?.add(JScrollPane(modelTree))
                    newFrame?.pack()
                    newFrame?.isVisible = true
                }
            }
        }
        add(newFrameButton)
    }


    SwingUtilities.invokeLater {
        val treeFrame = IntBinarySearchTreeFrame(treeModel)
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
