package com.example.mindvibe;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        bindSessionProfile(view);
        setupLogout(view);
        return view;
    }

    private void bindSessionProfile(View view) {
        String email = SessionManager.getEmail(requireContext());
        if (email.isEmpty()) {
            return;
        }

        bindMentalProfile(view, email);

        String displayName = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        if (displayName.isEmpty()) {
            displayName = "MindVibe";
        }
        TextView textProfileAvatar = view.findViewById(R.id.textProfileAvatar);
        TextView textProfileName = view.findViewById(R.id.textProfileName);
        TextView textProfileEmail = view.findViewById(R.id.textProfileEmail);

        textProfileName.setText(displayName);
        textProfileEmail.setText(email);
        textProfileAvatar.setText(displayName.substring(0, 1).toUpperCase(Locale.getDefault()));
    }

    private void bindMentalProfile(View view, String email) {
        MentalSnapshot snapshot = MentalSnapshot.fromEmail(email);

        setText(view, R.id.textMoodValue, snapshot.moodValue);
        setText(view, R.id.textMoodNote, snapshot.moodNote);
        setText(view, R.id.textToneValue, snapshot.toneValue);
        setText(view, R.id.textSleepValue, snapshot.sleepValue);
        setText(view, R.id.textSleepNote, snapshot.sleepNote);
        setText(view, R.id.textEnergyValue, snapshot.energyValue);
        setText(view, R.id.textStressValue, snapshot.stressValue);
        setText(view, R.id.textSocialValue, snapshot.socialValue);
        setText(view, R.id.textBodyValue, snapshot.bodyValue);
        setText(view, R.id.textCareTip, snapshot.careTip);

        ProgressBar progressTone = view.findViewById(R.id.progressTone);
        ProgressBar progressEnergy = view.findViewById(R.id.progressEnergy);
        progressTone.setProgress(snapshot.toneScore);
        progressEnergy.setProgress(snapshot.energyScore);

        setupSocialConnection(view, snapshot);
    }

    private void setText(View view, int textViewId, String value) {
        TextView textView = view.findViewById(textViewId);
        textView.setText(value);
    }

    private void setupSocialConnection(View view, MentalSnapshot snapshot) {
        View rowSocialConnection = view.findViewById(R.id.rowSocialConnection);
        rowSocialConnection.setOnClickListener(v -> showSocialMatches(snapshot));
    }

    private void showSocialMatches(MentalSnapshot snapshot) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_social_matches, null, false);

        TextView textMatchContext = sheetView.findViewById(R.id.textMatchContext);
        LinearLayout layoutMatchList = sheetView.findViewById(R.id.layoutMatchList);
        textMatchContext.setText("Loc theo: " + snapshot.moodValue
                + " | " + snapshot.energyValue
                + " | " + snapshot.socialValue);

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (SocialMatch match : getSocialMatches(snapshot)) {
            addSocialMatchView(inflater, layoutMatchList, match);
        }

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void addSocialMatchView(
            LayoutInflater inflater,
            LinearLayout container,
            SocialMatch match
    ) {
        View itemView = inflater.inflate(R.layout.item_social_match, container, false);

        TextView textFriendAvatar = itemView.findViewById(R.id.textFriendAvatar);
        TextView textFriendName = itemView.findViewById(R.id.textFriendName);
        TextView textMatchScore = itemView.findViewById(R.id.textMatchScore);
        TextView textFriendSummary = itemView.findViewById(R.id.textFriendSummary);
        TextView textFriendReason = itemView.findViewById(R.id.textFriendReason);
        TextView textFriendSignals = itemView.findViewById(R.id.textFriendSignals);
        TextView buttonAddFriend = itemView.findViewById(R.id.buttonAddFriend);

        textFriendAvatar.setText(match.initial);
        setAvatarColor(textFriendAvatar, match.colorRes);
        textFriendName.setText(match.name);
        textMatchScore.setText(match.matchScore);
        textFriendSummary.setText(match.summary);
        textFriendReason.setText(match.reason);
        textFriendSignals.setText(match.signals);
        buttonAddFriend.setOnClickListener(v -> {
            buttonAddFriend.setText("Da gui loi moi");
            buttonAddFriend.setEnabled(false);
            buttonAddFriend.setAlpha(0.72f);
            Toast.makeText(
                    requireContext(),
                    "Da gui loi moi ket ban den " + match.name,
                    Toast.LENGTH_SHORT
            ).show();
        });

        container.addView(itemView);
    }

    private void setAvatarColor(TextView avatarView, int colorRes) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        background.setColor(ContextCompat.getColor(requireContext(), colorRes));
        avatarView.setBackground(background);
    }

    private List<SocialMatch> getSocialMatches(MentalSnapshot snapshot) {
        List<SocialMatch> matches = new ArrayList<>();
        switch (snapshot.variant) {
            case 0:
                matches.add(new SocialMatch(
                        "An Nguyen",
                        "A",
                        R.color.vibe_teal,
                        "92%",
                        "Binh tam, dang can nguoi dong hanh nhe nhang",
                        "Cung nhom ap luc cong viec nhung van giu sac thai tich cuc.",
                        "Ngu 6h50 | Nang luong 7/10 | Stress deadline"
                ));
                matches.add(new SocialMatch(
                        "Minh Anh",
                        "M",
                        R.color.vibe_pink,
                        "88%",
                        "Thich noi chuyen ngan, ro rang va am ap",
                        "Co muc ket noi xa hoi gan giong va cung muon giu nhip cham.",
                        "Ngu 6h30 | Nang luong 6/10 | Can khoang lang"
                ));
                matches.add(new SocialMatch(
                        "Huy Lam",
                        "H",
                        R.color.vibe_orange,
                        "84%",
                        "Dang sap xep lai cong viec trong ngay",
                        "Tuong dong ve muc lo nhe va thoi quen chia nho viec.",
                        "Ngu 7h00 | Nang luong 7/10 | Ap luc lich hop"
                ));
                break;
            case 1:
                matches.add(new SocialMatch(
                        "Linh Tran",
                        "L",
                        R.color.vibe_pink,
                        "95%",
                        "Vui nhe, chu dong va de ket noi",
                        "Cung muc nang luong cao va thich trao doi tich cuc.",
                        "Ngu 7h15 | Nang luong 8/10 | Stress nhe"
                ));
                matches.add(new SocialMatch(
                        "Bao Chau",
                        "B",
                        R.color.vibe_teal,
                        "90%",
                        "Dang co dong luc, can ban cung tien do",
                        "Phu hop de cung check-in cong viec va giu thoi quen tot.",
                        "Ngu 7h30 | Nang luong 8/10 | Lich hop ngan"
                ));
                matches.add(new SocialMatch(
                        "Quang Minh",
                        "Q",
                        R.color.vibe_green,
                        "86%",
                        "Tinh tao, thich len ke hoach ngan",
                        "Tuong dong ve giac ngu sau va nhu cau duoc lang nghe.",
                        "Ngu 7h10 | Nang luong 8/10 | Viec nha nhe"
                ));
                break;
            case 2:
                matches.add(new SocialMatch(
                        "Thu Ha",
                        "T",
                        R.color.vibe_orange,
                        "91%",
                        "Tram, can noi chuyen cham va an toan",
                        "Cung co lo lang nhe va de ket noi qua nhung cau hoi ngan.",
                        "Ngu 5h45 | Nang luong 5/10 | Lo cong viec"
                ));
                matches.add(new SocialMatch(
                        "Duc Pham",
                        "D",
                        R.color.vibe_teal,
                        "87%",
                        "Dang can nguoi cung sap xep lai uu tien",
                        "Ho so gan ve muc can bang va nhu cau ro rang hon trong ngay.",
                        "Ngu 6h00 | Nang luong 5/10 | Ky vong cong viec"
                ));
                matches.add(new SocialMatch(
                        "Mai Lan",
                        "M",
                        R.color.vibe_pink,
                        "83%",
                        "Ngai bat dau nhung phan hoi rat nhe nhang",
                        "Phu hop cho ket noi it ap luc, khong can noi qua nhieu.",
                        "Ngu 5h50 | Nang luong 5/10 | Can duoc lang nghe"
                ));
                break;
            default:
                matches.add(new SocialMatch(
                        "Khanh Vy",
                        "K",
                        R.color.vibe_teal,
                        "89%",
                        "Can hoi phuc, uu tien ket noi nhe",
                        "Cung nhom nang luong thap va can cam giac khong bi thuc ep.",
                        "Ngu 8h10 | Nang luong 4/10 | Can nghi ngan"
                ));
                matches.add(new SocialMatch(
                        "Nam Hoang",
                        "N",
                        R.color.vibe_green,
                        "85%",
                        "Thich di bo, check-in ngan vao cuoi ngay",
                        "Tuong dong ve nhu cau hoi phuc va giam qua tai cam xuc.",
                        "Ngu 8h00 | Nang luong 4/10 | Cang thang ca nhan"
                ));
                matches.add(new SocialMatch(
                        "Phuong Linh",
                        "P",
                        R.color.vibe_pink,
                        "82%",
                        "Nhe nhang, ton trong khoang rieng",
                        "Phu hop khi ban muon ket noi ma khong can noi chuyen lien tuc.",
                        "Ngu 8h35 | Nang luong 4/10 | Hoi co lap"
                ));
                break;
        }
        return matches;
    }

    private void setupLogout(View view) {
        view.findViewById(R.id.rowLogout).setOnClickListener(v -> {
            SessionManager.clearSession(requireContext());
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private static class MentalSnapshot {
        final int variant;
        final String moodValue;
        final String moodNote;
        final String toneValue;
        final int toneScore;
        final String sleepValue;
        final String sleepNote;
        final String energyValue;
        final int energyScore;
        final String stressValue;
        final String socialValue;
        final String bodyValue;
        final String careTip;

        MentalSnapshot(
                int variant,
                String moodValue,
                String moodNote,
                String toneValue,
                int toneScore,
                String sleepValue,
                String sleepNote,
                String energyValue,
                int energyScore,
                String stressValue,
                String socialValue,
                String bodyValue,
                String careTip
        ) {
            this.variant = variant;
            this.moodValue = moodValue;
            this.moodNote = moodNote;
            this.toneValue = toneValue;
            this.toneScore = toneScore;
            this.sleepValue = sleepValue;
            this.sleepNote = sleepNote;
            this.energyValue = energyValue;
            this.energyScore = energyScore;
            this.stressValue = stressValue;
            this.socialValue = socialValue;
            this.bodyValue = bodyValue;
            this.careTip = careTip;
        }

        static MentalSnapshot fromEmail(String email) {
            int variant = Math.floorMod(email.toLowerCase(Locale.ROOT).hashCode(), 4);
            switch (variant) {
                case 0:
                    return new MentalSnapshot(
                            variant,
                            "Binh tam, hoi ap luc",
                            "Can mot khoang lang ngan",
                            "7/10 tich cuc",
                            7,
                            "6h40",
                            "Ngu vua du, de tinh giac",
                            "Kha on dinh",
                            7,
                            "Deadline va viec sap xep thoi gian",
                            "Co ket noi tot voi 1-2 nguoi than",
                            "Cang vai, dau nhe khi lam viec lau",
                            "Nghi 10 phut va viet ra 1 viec quan trong."
                    );
                case 1:
                    return new MentalSnapshot(
                            variant,
                            "Vui nhe, co dong luc",
                            "Tu tin hon sau khi chia nho viec",
                            "8/10 tich cuc",
                            8,
                            "7h20",
                            "Ngu sau, thuc day kha tinh tao",
                            "Cao",
                            8,
                            "Ap luc nhe tu lich hop va viec nha",
                            "Cam thay duoc lang nghe",
                            "Hoi moi mat vao cuoi ngay",
                            "Giu nhip lam viec 45 phut va dung man hinh som."
                    );
                case 2:
                    return new MentalSnapshot(
                            variant,
                            "Tram, co chut lo",
                            "Can ro rang hon ve viec phai lam",
                            "5/10 can bang",
                            5,
                            "5h50",
                            "Kho ngu luc dau dem",
                            "Thap vua",
                            5,
                            "Tai chinh va ky vong cong viec",
                            "Muon duoc noi chuyen nhung ngai bat dau",
                            "Met moi va chan an nhe",
                            "Chon 1 nguoi tin cay de nhan tin ngan hom nay."
                    );
                default:
                    return new MentalSnapshot(
                            variant,
                            "Met, can hoi phuc",
                            "Dang co dau hieu qua tai cam xuc",
                            "4/10 thap",
                            4,
                            "8h30",
                            "Ngu nhieu nhung chua that su sau",
                            "Thap",
                            4,
                            "Mau thuan quan he va ap luc ca nhan",
                            "Cam giac hoi co lap",
                            "Dau dau nhe, co the thieu nuoc",
                            "Giam viec moi, uong nuoc va di bo 5-10 phut."
                    );
            }
        }
    }

    private static class SocialMatch {
        final String name;
        final String initial;
        final int colorRes;
        final String matchScore;
        final String summary;
        final String reason;
        final String signals;

        SocialMatch(
                String name,
                String initial,
                int colorRes,
                String matchScore,
                String summary,
                String reason,
                String signals
        ) {
            this.name = name;
            this.initial = initial;
            this.colorRes = colorRes;
            this.matchScore = matchScore;
            this.summary = summary;
            this.reason = reason;
            this.signals = signals;
        }
    }
}
