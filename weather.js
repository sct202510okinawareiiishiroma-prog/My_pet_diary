/**
 * 選択された地域の天気情報を取得し、入力フォームに反映する
 */
async function applyRegionalWeather() {
	const regionSelect = document.getElementById('region-select');
	const previewArea = document.getElementById('weather-preview');
	if (!regionSelect) return;

	const coords = regionSelect.value.split(',');
	const lat = coords[0];
	const lon = coords[1];

	const url = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current=temperature_2m,relative_humidity_2m,weather_code&timezone=Asia/Tokyo`;

	try {
		const response = await fetch(url);
		if (!response.ok) throw new Error('Network response was not ok');

		const data = await response.json();
		const current = data.current;

		// --- ★追加：気象コードをマークに変換 ---
		let weatherMark = "";
		const code = current.weather_code;

		if (code === 0) {
			weatherMark = "☀️"; // 快晴
		} else if (code >= 1 && code <= 3) {
			weatherMark = "☁️"; // 晴れ〜曇り
		} else if (code >= 45 && code <= 48) {
			weatherMark = "🌫️"; // 霧
		} else if (code >= 51 && code <= 67 || code >= 80 && code <= 82) {
			weatherMark = "☔"; // 雨・しとしと雨
		} else if (code >= 71 && code <= 77 || code >= 85 && code <= 86) {
			weatherMark = "❄️"; // 雪
		} else if (code >= 95 && code <= 99) {
			weatherMark = "⚡"; // 雷雨
		} else {
			weatherMark = "❓";
		}
		
		// 取得した気温 湿度 天気を出力
		if (previewArea) {
			previewArea.textContent = `現在：${current.temperature_2m}°C / ${current.relative_humidity_2m}% ${weatherMark}`;
		}
		
		// 気温と湿度を入力欄にセット
		document.getElementById('temp-input').value = current.temperature_2m;
		document.getElementById('humi-input').value = current.relative_humidity_2m;

		const regionName = regionSelect.options[regionSelect.selectedIndex].text;
		console.log(`${regionName}の天気を反映しました`);

	} catch (error) {
		console.error("天気取得エラー:", error);
		alert("天気情報の取得に失敗しました。");
	}
}