package com.targis.magicembed.ui.draw

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.targis.magicembed.MainActions
import com.targis.magicembed.MainDestinations
import com.targis.magicembed.R
import com.targis.magicembed.ui.theme.Purple700

@Composable
fun DrawerView(
    mainActions: MainActions,
    closeDrawer: () -> Unit,
    currentRoute: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp), verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Text(
            text = "MagicEmbed",
            fontSize = 30.sp,
            color = Purple700,
            fontWeight = FontWeight.Light
        )
        Divider(color = Color.LightGray, thickness = 1.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        DrawerButton(
            text = "Home",
            imageVector = Icons.Outlined.Home,
            route = MainDestinations.HOME_ROUTE,
            currentRoute = currentRoute,
            closeDrawer = closeDrawer,
            action = mainActions.navigateToHome
        )
        DrawerButton(
            text = "Serial",
            painter = painterResource(id = R.drawable.ic_usb_black_24dp),
            route = MainDestinations.Serial,
            currentRoute = currentRoute,
            closeDrawer = closeDrawer,
            action = mainActions.navigateToSerial
        )
        DrawerButton(
            text = "Help",
            painter = painterResource(id = R.drawable.help_outline_black_24dp),
            route = MainDestinations.HELP,
            currentRoute = currentRoute,
            closeDrawer = closeDrawer,
            action = mainActions.navigateToHelp
        )
        DrawerButton(
            text = "About",
            imageVector = Icons.Outlined.Info,
            route = MainDestinations.ABOUT,
            currentRoute = currentRoute,
            closeDrawer = closeDrawer,
            action = mainActions.navigateToAbout
        )
    }
}

@Composable
fun DrawerButton(
    text: String,
    imageVector: ImageVector,
    action: () -> Unit,
    closeDrawer: () -> Unit,
    route: String,
    currentRoute: String
) {
    DrawerButton(
        text = text,
        action = action,
        closeDrawer = closeDrawer,
        route = route,
        currentRoute = currentRoute,

        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 20.dp)
            )
        }
    )
}


@Composable
fun DrawerButton(
    text: String,
    painter: Painter,
    action: () -> Unit,
    closeDrawer: () -> Unit,
    route: String,
    currentRoute: String
) {
    DrawerButton(
        text = text,
        action = action,
        closeDrawer = closeDrawer,
        route = route,
        currentRoute = currentRoute,

        icon = {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .padding(end = 20.dp)
            )
        }
    )
}

@Composable
fun DrawerButton(
    text: String,
    icon: @Composable () -> Unit,
    action: () -> Unit,
    closeDrawer: () -> Unit,
    route: String,
    currentRoute: String
) {
    Surface(
        modifier = Modifier.clip(RoundedCornerShape(10.dp)),
        shape = MaterialTheme.shapes.medium

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,

            modifier = Modifier
                .fillMaxWidth()
                .background(if (currentRoute == route) Color.LightGray else Color.Transparent)
                .clickable {
                    action()
                    closeDrawer()
                }
                .padding(5.dp)
        ) {
            icon()
            Text(text = text, fontSize = 20.sp, fontWeight = FontWeight.Light)
        }
    }
}