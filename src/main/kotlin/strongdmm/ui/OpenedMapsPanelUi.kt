package strongdmm.ui

import imgui.ImGui.*
import imgui.enums.ImGuiCol
import imgui.enums.ImGuiCond
import strongdmm.byond.dmm.Dmm
import strongdmm.event.Event
import strongdmm.event.EventConsumer
import strongdmm.event.EventSender
import strongdmm.event.type.EventGlobal
import strongdmm.event.type.EventGlobalProvider
import strongdmm.event.type.controller.EventMapHolderController
import strongdmm.util.imgui.RED32
import strongdmm.util.imgui.setItemHoveredTooltip
import strongdmm.util.imgui.smallButton
import strongdmm.util.imgui.window
import strongdmm.window.AppWindow

class OpenedMapsPanelUi : EventConsumer, EventSender {
    private var openedMaps: Set<Dmm> = emptySet()
    private var selectedMap: Dmm? = null

    init {
        consumeEvent(EventGlobalProvider.OpenedMaps::class.java, ::handleProviderOpenedMaps)
        consumeEvent(EventGlobal.SelectedMapChanged::class.java, ::handleSelectedMapChanged)
        consumeEvent(EventGlobal.OpenedMapClosed::class.java, ::handleOpenedMapClosed)
    }

    fun process() {
        if (openedMaps.isEmpty()) {
            return
        }

        setNextWindowPos(AppWindow.windowWidth - 160f, 30f, ImGuiCond.Once)
        setNextWindowSize(150f, 150f, ImGuiCond.Once)
        setNextWindowCollapsed(true, ImGuiCond.Once)

        window("${selectedMap?.mapName}###opened_maps") {
            openedMaps.toTypedArray().forEach { map ->
                pushStyleColor(ImGuiCol.ButtonHovered, RED32)
                smallButton("X##close_map_${map.visibleMapPath}") {
                    sendEvent(EventMapHolderController.CloseMap(map.id))
                }
                popStyleColor()

                sameLine()

                if (selectable(map.mapName, selectedMap === map)) {
                    if (selectedMap !== map) {
                        sendEvent(EventMapHolderController.ChangeSelectedMap(map.id))
                    }
                }
                setItemHoveredTooltip(map.visibleMapPath)
            }
        }
    }

    private fun handleProviderOpenedMaps(event: Event<Set<Dmm>, Unit>) {
        openedMaps = event.body
    }

    private fun handleSelectedMapChanged(event: Event<Dmm, Unit>) {
        selectedMap = event.body
    }

    private fun handleOpenedMapClosed(event: Event<Dmm, Unit>) {
        if (event.body === selectedMap) {
            selectedMap = null
        }
    }
}
