package com.ptithcm.manga.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ptithcm.manga.R;
import com.ptithcm.manga.data.model.request.ChapterRequest;
import com.ptithcm.manga.data.model.response.ChapterResponse;
import com.ptithcm.manga.data.model.response.UploadResponse;
import com.ptithcm.manga.data.repository.AdminRepository;
import com.ptithcm.manga.data.repository.ChapterRepository;
import com.ptithcm.manga.data.repository.UploadRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminChapterFormFragment extends Fragment {

    private AdminRepository adminRepository;
    private ChapterRepository chapterRepository;
    private UploadRepository uploadRepository;

    private RecyclerView rvChapters;
    private ChapterListAdapter adapter;
    private int mangaId = -1;
    private final List<ChapterResponse> chapterList = new ArrayList<>();
    private final List<String> pendingImageUrls = new ArrayList<>(); // for new chapter upload

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_chapter_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adminRepository = new AdminRepository(requireContext());
        chapterRepository = new ChapterRepository(requireContext());
        uploadRepository = new UploadRepository(requireContext());

        if (getArguments() != null) {
            mangaId = getArguments().getInt("mangaId", -1);
        }

        rvChapters = view.findViewById(R.id.rv_chapters);
        adapter = new ChapterListAdapter();
        rvChapters.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvChapters.setAdapter(adapter);

        view.findViewById(R.id.fab_add_chapter).setOnClickListener(v -> showAddChapterDialog());

        loadChapters();
    }

    private void loadChapters() {
        chapterRepository.getChaptersByMangaId(mangaId, new ChapterRepository.RepositoryCallback<List<ChapterResponse>>() {
            @Override
            public void onSuccess(List<ChapterResponse> data) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    chapterList.clear();
                    chapterList.addAll(data);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showAddChapterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_chapter_form, null);
        builder.setView(dialogView);
        builder.setTitle("Thêm chapter mới");

        EditText etNumber = dialogView.findViewById(R.id.et_chapter_number);
        EditText etTitle = dialogView.findViewById(R.id.et_chapter_title);
        RecyclerView rvImages = dialogView.findViewById(R.id.rv_images);
        View btnAddImage = dialogView.findViewById(R.id.btn_add_image);

        final List<String> imageUrls = new ArrayList<>();
        final ImageListAdapter[] imageAdapterHolder = new ImageListAdapter[1];
        imageAdapterHolder[0] = new ImageListAdapter(imageUrls, url -> {
            int idx = imageUrls.indexOf(url);
            imageUrls.remove(url);
            imageAdapterHolder[0].notifyItemRemoved(idx);
        });
        ImageListAdapter imageAdapter = imageAdapterHolder[0];
        rvImages.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(imageAdapter);

        btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickLauncher.launch(intent);
        });

        // Store reference for image picker callback to use
        final List<String> refImageUrls = imageUrls;
        final ImageListAdapter refAdapter = imageAdapter;

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String numStr = etNumber.getText().toString().trim();
            String title = etTitle.getText().toString().trim();

            if (numStr.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập số chapter", Toast.LENGTH_SHORT).show();
                return;
            }

            double chapterNumber;
            try {
                chapterNumber = Double.parseDouble(numStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Số chapter không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            ChapterRequest request = new ChapterRequest(
                    title.isEmpty() ? null : title,
                    chapterNumber,
                    refImageUrls.isEmpty() ? null : new ArrayList<>(refImageUrls));

            adminRepository.createChapter(mangaId, request, new AdminRepository.AdminCallback<ChapterResponse>() {
                @Override
                public void onSuccess(ChapterResponse data) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Đã thêm chapter", Toast.LENGTH_SHORT).show();
                        loadChapters();
                    });
                }

                @Override
                public void onError(String message) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                }
            });
        });

        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void deleteChapter(int chapterId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa chapter")
                .setMessage("Xóa chapter này? Tất cả ảnh trang sẽ bị xóa.")
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    adminRepository.deleteChapter(chapterId, new AdminRepository.AdminCallback<Object>() {
                        @Override
                        public void onSuccess(Object data) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Đã xóa chapter", Toast.LENGTH_SHORT).show();
                                loadChapters();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private final ActivityResultLauncher<Intent> imagePickLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    // Single image pick for simplicity
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        uploadImageForChapter(uri);
                    }
                }
            });

    private void uploadImageForChapter(Uri uri) {
        Toast.makeText(requireContext(), "Đang upload ảnh...", Toast.LENGTH_SHORT).show();
        uploadRepository.uploadImage(uri, "chapter_pages").enqueue(new Callback<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>>() {
            @Override
            public void onResponse(@NonNull Call<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> call,
                                   @NonNull Response<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String url = response.body().getData().secure_url;
                    requireActivity().runOnUiThread(() -> {
                        pendingImageUrls.add(url);
                        Toast.makeText(requireContext(), "Upload ảnh thành công", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> call,
                                  @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Upload thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    // Simple adapter for existing chapters
    private class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chapter, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ChapterResponse ch = chapterList.get(position);
            String label = "Ch." + ch.getChapterNumber();
            if (ch.getTitle() != null && !ch.getTitle().isEmpty()) {
                label += ": " + ch.getTitle();
            }
            holder.tvNumber.setText(label);
            holder.tvTitle.setText(ch.getViewCount() + " trang");
            holder.itemView.setOnLongClickListener(v -> {
                deleteChapter(ch.getId());
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return chapterList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            android.widget.TextView tvNumber, tvTitle;
            VH(@NonNull View itemView) {
                super(itemView);
                tvNumber = itemView.findViewById(R.id.tv_chapter_number);
                tvTitle = itemView.findViewById(R.id.tv_chapter_title);
            }
        }
    }

    // Simple adapter for image list in dialog
    private static class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.IH> {
        private final List<String> urls;
        private final OnRemoveListener listener;

        interface OnRemoveListener { void onRemove(String url); }

        ImageListAdapter(List<String> urls, OnRemoveListener listener) {
            this.urls = urls;
            this.listener = listener;
        }

        @NonNull
        @Override
        public IH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new ViewGroup.LayoutParams(120, 160));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(4, 4, 4, 4);
            return new IH(iv);
        }

        @Override
        public void onBindViewHolder(@NonNull IH holder, int position) {
            Glide.with(holder.iv.getContext()).load(urls.get(position)).into(holder.iv);
            holder.iv.setOnLongClickListener(v -> {
                listener.onRemove(urls.get(position));
                return true;
            });
        }

        @Override
        public int getItemCount() { return urls.size(); }

        static class IH extends RecyclerView.ViewHolder {
            ImageView iv;
            IH(@NonNull View itemView) { super(itemView); iv = (ImageView) itemView; }
        }
    }
}
