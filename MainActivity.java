package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class MainActivity extends AppCompatActivity {

    // UI 요소들을 위한 변수 선언
    ChipGroup chipGroupVolume, chipGroupQuantity; // 수량 칩 그룹 추가
    EditText editTextCustomVolume, editTextCustomQuantity, editTextPrice; // 수량 입력 EditText 추가
    Button buttonCalculate;
    TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML ID와 자바 변수 연결
        chipGroupVolume = findViewById(R.id.chipGroupVolume);
        editTextCustomVolume = findViewById(R.id.editTextCustomVolume);
        chipGroupQuantity = findViewById(R.id.chipGroupQuantity); // 수량 칩 그룹 연결
        editTextCustomQuantity = findViewById(R.id.editTextCustomQuantity); // 수량 EditText 연결
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonCalculate = findViewById(R.id.buttonCalculate);
        textViewResult = findViewById(R.id.textViewResult);

        // '계산하기' 버튼 클릭 리스너
        buttonCalculate.setOnClickListener(v -> calculatePricePer100ml());

        // 용량 직접 입력 시, 용량 칩 선택 해제
        editTextCustomVolume.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) chipGroupVolume.clearCheck();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // 수량 칩 그룹 선택 리스너 (선택 시 EditText에 자동 입력)
        chipGroupQuantity.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip selectedChip = findViewById(checkedId);
                String quantityText = selectedChip.getText().toString().replaceAll("[^0-9]", "");
                editTextCustomQuantity.setText(quantityText);
            }
        });
    }

    // 실제 계산 로직을 수행하는 메소드
    private void calculatePricePer100ml() {
        // (1) 개당 용량 가져오기
        int singleVolume = 0;
        String customVolumeText = editTextCustomVolume.getText().toString();
        if (!customVolumeText.isEmpty()) {
            singleVolume = Integer.parseInt(customVolumeText);
        } else {
            int selectedChipId = chipGroupVolume.getCheckedChipId();
            if (selectedChipId != View.NO_ID) {
                Chip selectedChip = findViewById(selectedChipId);
                String volumeText = selectedChip.getText().toString().replaceAll("[^0-9]", "");
                singleVolume = Integer.parseInt(volumeText);
            }
        }

        if (singleVolume <= 0) {
            Toast.makeText(this, "개당 용량을 선택하거나 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // (2) 수량 가져오기 (입력 없으면 기본 1개)
        int quantity = 1;
        String quantityText = editTextCustomQuantity.getText().toString();
        if (!quantityText.isEmpty()) {
            quantity = Integer.parseInt(quantityText);
        }

        if (quantity <= 0) {
            Toast.makeText(this, "수량은 1 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // (3) 총 용량 계산
        int totalVolume = singleVolume * quantity;

        // (4) 총 가격 가져오기
        String priceText = editTextPrice.getText().toString();
        if (priceText.isEmpty()) {
            Toast.makeText(this, "총 가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        double totalPrice = Double.parseDouble(priceText);

        // (5) 100ml 당 가격 계산 (반올림)
        double pricePer100ml = (totalPrice / totalVolume) * 100;
        long roundedPrice = Math.round(pricePer100ml);

        // (6) 결과 표시
        String resultString = String.format("결과: %d 원 / 100ml", roundedPrice);
        textViewResult.setText(resultString);
    }
}