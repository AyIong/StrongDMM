package io.github.spair.strongdmm.gui.util

import io.github.spair.strongdmm.DI
import io.github.spair.strongdmm.gui.PrimaryFrame
import org.kodein.di.direct
import org.kodein.di.erased.instance
import java.awt.BorderLayout
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.concurrent.thread

private val FRAME = DI.direct.instance<PrimaryFrame>().windowFrame

fun chooseFileDialog(desc: String, ext: String, root: String = "."): File? {
    val fileChooser = JFileChooser(root).apply {
        isAcceptAllFileFilterUsed = false
        addChoosableFileFilter(FileNameExtensionFilter(desc, ext))
    }

    return if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        fileChooser.selectedFile
    } else {
        null
    }
}

// Blocks main frame and does some blocking stuff while showing indeterminate progress bar
fun runWithProgressBar(progressText: String, action: () -> Unit) {
    val dialog = JDialog(FRAME, null, true).apply {
        add(BorderLayout.NORTH, JLabel(progressText).apply { border = EmptyBorder(5, 5, 5, 5) })
        add(BorderLayout.SOUTH, JProgressBar().apply { isIndeterminate = true })

        setSize(300, 75)
        setLocationRelativeTo(FRAME)
        defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
    }

    thread(start = true) {
        action()
        dialog.isVisible = false
    }

    dialog.isVisible = true
}

fun showAvailableMapsDialog(availableMaps: List<String>): String? {
    val dmmList = JList(availableMaps.toTypedArray())
    val dialogPane = JScrollPane(dmmList)
    val res = JOptionPane.showConfirmDialog(FRAME, dialogPane, "Select map to open", JOptionPane.OK_CANCEL_OPTION)
    return if (res != JOptionPane.CANCEL_OPTION) {
        dmmList.selectedValue
    } else {
        null
    }
}
