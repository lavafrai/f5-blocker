package ru.lavafrai.svogame.f5blocker.network

import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import ru.lavafrai.svogame.f5blocker.config.F5ClientState
import java.util.function.Supplier


class UpdateClientStatePacket(
    private val isBlocked: Boolean
) {
    constructor(buf: FriendlyByteBuf): this(buf.readBoolean())

    fun encode(buf: FriendlyByteBuf) {
        buf.writeBoolean(isBlocked)
    }

    fun isF5Blocked(): Boolean = isBlocked
    fun isF5Allowed(): Boolean = !isBlocked

    companion object {
        fun handle(
            msg: UpdateClientStatePacket,
            ctxSupplier: Supplier<NetworkEvent.Context>
        ) {
            val ctx = ctxSupplier.get()

            ctx.enqueueWork {
                F5ClientState.setF5Blocked(msg.isBlocked)

                if (msg.isBlocked) {
                    val mc = Minecraft.getInstance()
                    val options = mc.options ?: return@enqueueWork

                    if (options.cameraType != CameraType.FIRST_PERSON) {
                        options.cameraType = CameraType.FIRST_PERSON
                    }
                }
            }

            ctx.packetHandled = true
        }
    }
}