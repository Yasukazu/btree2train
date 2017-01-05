/**
 * Created by Yasukazu on 2016/12/29.
 * GUI main outside from the package
 * 2016-12-29 BinarySearchTreeFrame (accepts General type)
 */
import jp.yasukazu.kotlin.train.BinarySearchTreeModel
import jp.yasukazu.kotlin.train.BinarySearchTreeFrame
import jp.yasukazu.kotlin.train.KollationKey
import java.awt.Rectangle
import java.io.FileOutputStream
import java.io.ObjectOutputStream
//import java.text.CollationKey
import java.text.Collator
import java.text.Normalizer
import java.util.*
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.SwingUtilities


fun main(args:Array<String>){
    val treeModel = BinarySearchTreeModel<Float>()
    args.forEach { a ->
        val i = try { a.toFloat() } catch (e: NumberFormatException){
            println("$a caused $e")
            null }
        if (i != null){
            treeModel += i
        }
    }
    val stringTreeModel = BinarySearchTreeModel<String>()
    args.forEach { i ->
            stringTreeModel += i
    }

    val kollatorTreeModel = BinarySearchTreeModel<KollationKey>()
    fun fromStringToKollationKey(it: String): KollationKey {
        val collator = Collator.getInstance()//Locale.JAPANESE)
        collator.strength = Collator.SECONDARY
        return KollationKey(collator.getCollationKey(Normalizer.normalize((it), Normalizer.Form.NFKC)))
    }
    args.forEach {it ->
        kollatorTreeModel += fromStringToKollationKey(it)
    }

    SwingUtilities.invokeLater {
        val intTreeFrame = BinarySearchTreeFrame(treeModel){it.toFloat()} // catch (e:NumberFormatException){null}}
        with(intTreeFrame){
            title = "Real Number Tree"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setBounds(0, 0, 400, 600)
            pack()
            isVisible = true
        }
        val stringTreeFrame = BinarySearchTreeFrame(stringTreeModel){it}
        with(stringTreeFrame){
            title = "String Tree"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setBounds(50, 50, 400, 600)
            pack()
            isVisible = true
        }
        val kollationKeyFrame = BinarySearchTreeFrame(kollatorTreeModel, ::fromStringToKollationKey)
        with(kollationKeyFrame){
            val list = kollatorTreeModel.preTraverseList()
            val filename = "___outputStream___.$$$"
            val serializeButton = JButton("Serialize to $filename")
            serializeButton.addActionListener {
                FileOutputStream(filename).use { output ->
                    ObjectOutputStream(output).use { oo ->
                        oo.writeObject(list)
                    }
                }
            }
            entryPanel.add(serializeButton)
            title = "Collation String Tree"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setBounds(100, 100, 400, 600)
            pack()
            isVisible = true
        }
    }
}

