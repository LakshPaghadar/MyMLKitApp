import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.laksh.mydocscannerapp.composescreen.BaseTopAppBar

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetectFaces() {
    Scaffold(
        topBar = {
            BaseTopAppBar(title = "Detect Faces"){

            }
        },
        content = { it->
            ScrollContent(it)
        }
    )
}

@Composable
private fun ScrollContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Detect Faces")
    }
}