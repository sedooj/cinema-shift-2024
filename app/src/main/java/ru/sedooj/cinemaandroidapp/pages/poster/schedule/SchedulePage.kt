package ru.sedooj.cinemaandroidapp.pages.poster.schedule

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ru.sedooj.cinemaandroidapp.R
import ru.sedooj.cinemaandroidapp.navigation.Screens
import ru.sedooj.cinemaandroidapp.network.Client
import ru.sedooj.cinemaandroidapp.network.Data
import ru.sedooj.cinemaandroidapp.network.cinema.film.GetFilmByIdInput
import ru.sedooj.cinemaandroidapp.network.cinema.film.GetFilmByIdOutput
import ru.sedooj.cinemaandroidapp.network.cinema.film.schedule.GetFilmScheduleByIdInput
import ru.sedooj.cinemaandroidapp.network.cinema.film.schedule.GetFilmScheduleByIdOutput
import ru.sedooj.cinemaandroidapp.network.cinema.repository.CinemaNetworkRepositoryImpl
import ru.sedooj.cinemaandroidapp.ui.design.pages.NavigationBackButton
import ru.sedooj.cinemaandroidapp.ui.design.pages.PageDataLoadingComponent
import ru.sedooj.cinemaandroidapp.ui.design.pages.ScrollableCenteredScreenContentComponent

fun String.translate(): String {
    return when (this) {
        "Red" -> "Красный зал"
        "Blue" -> "Синий зал"
        "Green" -> "Зелёный зал"
        else -> {
            this
        }
    }
}

data class SelectedHallTimeState(
    var hall: String = "",
    var time: String = "",
    var date: String = "",
    var places: List<List<Place>>
) {

    data class Place(
        var price: Int,
        var type: String
    )

}

val filmState = mutableStateOf<GetFilmByIdOutput?>(null)
val scheduleState = mutableStateOf<GetFilmScheduleByIdOutput?>(null)
val selectedSeanceState = mutableStateOf<SelectedHallTimeState?>(null)

data class SchedulePageState(
    val _selectedSeanceState: MutableState<SelectedHallTimeState?> = selectedSeanceState
)

@Composable
fun SchedulePage(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    filmId: Long?,
    navController: NavController,
) {
    BackHandler(onBack = {
        onBack()
        navController.popBackStack()
    })
    ScrollableCenteredScreenContentComponent(
        modifier = modifier,
        mainPaddingValue = padding,
        title = Screens.SCHEDULE.pageName,
        navigationIcon = {
            NavigationBackButton(navController = navController, subAction = { onBack() })
        },
        floatingActionButton = {
            if (selectedSeanceState.value != null) {

                FloatingActionButton(
                    contentColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.inversePrimary,
                    onClick = {
                        navController.navigate(Screens.POSITION.route)
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Back",
                        modifier = Modifier.rotate(180f)
                    )
                }
            }
        },
        content = {
            if (filmId != null) {
                val client = remember { Client.create() }
                val cinemaNetworkRepository =
                    remember { CinemaNetworkRepositoryImpl(client = client) }

                FilmPreviewComponent(
                    filmId = filmId,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    cinemaNetworkRepository = cinemaNetworkRepository
                )

                ScheduleDataComponent(
                    filmId = filmId,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    cinemaNetworkRepository = cinemaNetworkRepository
                )

            }


        }
    )
}


@Composable
private fun FilmPreviewComponent(
    filmId: Long,
    modifier: Modifier,
    cinemaNetworkRepository: CinemaNetworkRepositoryImpl
) {

    if (filmState.value == null) {
        PageDataLoadingComponent(
            title = "Загрузка данных фильма..."
        )
        LaunchedEffect(key1 = filmState.value == null) {
            filmState.value =
                cinemaNetworkRepository.getFilmById(input = GetFilmByIdInput(id = filmId))
        }
    } else {
        Text(
            text = "Фильм",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            fontWeight = FontWeight.ExtraBold,
        )
        Card(
            modifier = modifier,
            content = {
                filmState.value?.film?.let { film ->
                    FilmDataComponent(
                        img = film.img, title = film.name, description = film.description
                    )
                }
            }

        )
    }
}

@Composable
private fun FilmDataComponent(
    img: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = {
            // Poster
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center,
                content = {
                    AsyncImage(
                        model = "${Data.BASE_URL}$img",
                        contentDescription = "Image",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxSize()
                            .sizeIn(
                                minWidth = 50.dp,
                                minHeight = 50.dp,
                                maxHeight = 100.dp,
                                maxWidth = 200.dp
                            )
                            .clip(shape = RoundedCornerShape(10.dp)),
                    )
                }
            )
            // Data
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(4f),
                contentAlignment = Alignment.Center,
                content = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Title
                        Text(
                            text = title,
                            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                            fontWeight = FontWeight.ExtraBold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        // Description
                        Text(
                            text = description,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 3
                        )
                    }
                }
            )

        }
    )
}


@Composable
private fun ScheduleDataComponent(
    filmId: Long,
    modifier: Modifier,
    cinemaNetworkRepository: CinemaNetworkRepositoryImpl
) {
    if (scheduleState.value == null) {
        PageDataLoadingComponent(
            title = "Загрузка расписания фильма..."
        )
        LaunchedEffect(key1 = scheduleState.value == null) {
            scheduleState.value = cinemaNetworkRepository.getFilmScheduleById(
                input = GetFilmScheduleByIdInput(
                    id = filmId
                )
            )
        }
    } else {
        val horizontalScrollState = rememberScrollState()
        val selectedDateState = remember { mutableIntStateOf(0) }

        SelectableDateComponent(
            modifier = modifier.fillMaxWidth(),
            scheduleState = scheduleState.value,
            horizontalScrollState = horizontalScrollState,
            onSelect = {
                if (selectedDateState.intValue != it)
                    selectedDateState.intValue = it
            },
            selectedDate = selectedDateState.intValue
        )
        scheduleState.value?.schedules?.forEachIndexed { index, schedule ->
            if (selectedDateState.intValue == index) {
                var currentHall = ""
                schedule.seances.forEachIndexed { id, seance ->
                    if (currentHall != seance.hall.name) {
                        Text(seance.hall.name.translate())
                        currentHall = seance.hall.name
                    }

                    SelectableButtonComponent(
                        onClick = {
                            if (selectedSeanceState.value?.time != seance.time || selectedSeanceState.value?.hall != schedule.date || selectedSeanceState.value?.hall != seance.hall.name) {
                                selectedSeanceState.value = SelectedHallTimeState(
                                    time = seance.time,
                                    hall = seance.hall.name,
                                    date = schedule.date,
                                    places = seance.hall.places.map {
                                        it.map { place ->
                                            SelectedHallTimeState.Place(
                                                price = place.price,
                                                type = place.type
                                            )
                                        }
                                    }
                                )
                            }
                        },
                        text = seance.time,
                        isSelected = selectedSeanceState.value?.time == seance.time && selectedSeanceState.value?.date == schedule.date && selectedSeanceState.value?.hall == seance.hall.name,
                        colors = SelectableButtonColors(
                            selected = MaterialTheme.colorScheme.inversePrimary,
                            notSelected = MaterialTheme.colorScheme.inverseOnSurface
                        ),
                        modifier = modifier
                    )
                }
            }
        }
    }
}

private data class SelectableButtonColors(
    val selected: Color = Color.DarkGray,
    val notSelected: Color = Color.LightGray,
    val disabled: Color = Color.Unspecified
)


@Composable
private fun SelectableDateComponent(
    modifier: Modifier,
    scheduleState: GetFilmScheduleByIdOutput?,
    horizontalScrollState: ScrollState,
    onSelect: (Int) -> Unit,
    selectedDate: Int
) {
    Text(
        text = "Даты",
        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
        fontWeight = FontWeight.ExtraBold,
    )
    Card(
        modifier = modifier
            .wrapContentHeight()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.horizontalScroll(state = horizontalScrollState)
        ) {
            scheduleState?.schedules?.forEachIndexed { index, schedule ->
                SelectableButtonComponent(
                    onClick = {
                        onSelect(index)
                    },
                    text = schedule.date,
                    isSelected = selectedDate == index,
                    colors = SelectableButtonColors(
                        selected = MaterialTheme.colorScheme.background,
                        notSelected = Color.Unspecified
                    ),
                    modifier = modifier
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectableButtonComponent(
    onClick: () -> Unit,
    text: String,
    isSelected: Boolean,
    colors: SelectableButtonColors,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
            .height(40.dp)
            .padding(3.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colors.selected else colors.notSelected,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        onClick = {
            onClick()
        },
        content = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = text, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
            }
        }
    )
}

private fun onBack() {
    filmState.value = null
    scheduleState.value = null
    selectedSeanceState.value = null
}