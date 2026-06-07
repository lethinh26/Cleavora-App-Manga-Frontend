package com.ptithcm.manga.ui.admin;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

    // Danh sách ảnh với trạng thái upload
    private final List<ImageItem> imageItems = new ArrayList<>();
    private ImagePreviewAdapter imagePreviewAdapter;

    // Chapter đang chỉnh sửa
    private ChapterResponse editingChapter = null;

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

        view.findViewById(R.id.fab_add_chapter).setOnClickListener(v -> {
            editingChapter = null;
            showChapterDialog(null);
        });

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

    // ========= Dialog Thêm / Sửa Chapter =========
    private void showChapterDialog(@Nullable ChapterResponse chapter) {
        boolean isEdit = (chapter != null);
        imageItems.clear();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_chapter_form, null);
        builder.setView(dialogView);
        builder.setTitle(isEdit ? "Sửa chapter" : "Thêm chapter mới");

        EditText etNumber = dialogView.findViewById(R.id.et_chapter_number);
        EditText etTitle = dialogView.findViewById(R.id.et_chapter_title);
        RecyclerView rvImages = dialogView.findViewById(R.id.rv_images);
        View btnAddImage = dialogView.findViewById(R.id.btn_add_image);

        // Điền sẵn dữ liệu cũ khi edit
        if (isEdit) {
            if (chapter.getChapterNumber() != null) {
                etNumber.setText(com.ptithcm.manga.util.ChapterFormatter.format(chapter.getChapterNumber()));
            }
            if (chapter.getTitle() != null) {
                etTitle.setText(chapter.getTitle());
            }
        }

        imagePreviewAdapter = new ImagePreviewAdapter(imageItems, previewUrl -> {
            for (int i = 0; i < imageItems.size(); i++) {
                if (imageItems.get(i).previewUrl.equals(previewUrl)) {
                    imageItems.remove(i);
                    imagePreviewAdapter.notifyItemRemoved(i);
                    break;
                }
            }
        });
        rvImages.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(imagePreviewAdapter);

        // Nếu là edit: load ảnh cũ từ API
        if (isEdit) {
            chapterRepository.getChapterDetail(chapter.getId(), new ChapterRepository.RepositoryCallback<com.ptithcm.manga.data.model.response.ChapterDetailResponse>() {
                @Override
                public void onSuccess(com.ptithcm.manga.data.model.response.ChapterDetailResponse data) {
                    if (!isAdded() || data == null || data.getImages() == null) return;
                    requireActivity().runOnUiThread(() -> {
                        for (com.ptithcm.manga.data.model.response.ChapterImageResponse img : data.getImages()) {
                            // Ảnh cũ: previewUrl = cloudUrl, uploaded = true
                            ImageItem item = new ImageItem(img.getImageUrl());
                            item.cloudUrl = img.getImageUrl();
                            item.uploaded = true;
                            imageItems.add(item);
                        }
                        imagePreviewAdapter.notifyDataSetChanged();
                    });
                }

                @Override
                public void onError(String message) {
                    // Không chặn dialog nếu load ảnh cũ lỗi
                }
            });
        }

        btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            imagePickLauncher.launch(intent);
        });

        AlertDialog dialog = builder.create();

        dialog.setButton(AlertDialog.BUTTON_POSITIVE, isEdit ? "Cập nhật" : "Lưu", (d, which) -> {
            // handled below
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel), (d, which) -> {});

        dialog.show();

        // Override positive button để tránh dialog tự đóng khi còn upload
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String numStr = etNumber.getText().toString().trim();
            String titleStr = etTitle.getText().toString().trim();

            if (numStr.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập số chapter", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra còn ảnh đang upload không
            for (ImageItem item : imageItems) {
                if (!item.uploaded && !item.failed) {
                    Toast.makeText(requireContext(), "Vui lòng chờ ảnh upload xong!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            double chapterNumber;
            try {
                chapterNumber = Double.parseDouble(numStr);
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "Số chapter không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> finalUrls = new ArrayList<>();
            for (ImageItem item : imageItems) {
                if (item.uploaded && item.cloudUrl != null) {
                    finalUrls.add(item.cloudUrl);
                }
            }

            ChapterRequest request = new ChapterRequest(
                    titleStr.isEmpty() ? null : titleStr,
                    chapterNumber,
                    finalUrls.isEmpty() ? null : finalUrls);

            dialog.dismiss();

            if (isEdit) {
                adminRepository.updateChapter(chapter.getId(), request, new AdminRepository.AdminCallback<ChapterResponse>() {
                    @Override
                    public void onSuccess(ChapterResponse data) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Đã cập nhật chapter!", Toast.LENGTH_SHORT).show();
                            loadChapters();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_LONG).show());
                    }
                });
            } else {
                adminRepository.createChapter(mangaId, request, new AdminRepository.AdminCallback<ChapterResponse>() {
                    @Override
                    public void onSuccess(ChapterResponse data) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Đã thêm chapter thành công!", Toast.LENGTH_SHORT).show();
                            loadChapters();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_LONG).show());
                    }
                });
            }
        });
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

    // ========= Image Picker =========
    private final ActivityResultLauncher<Intent> imagePickLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    List<Uri> uriList = new ArrayList<>();

                    ClipData clipData = result.getData().getClipData();
                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            uriList.add(clipData.getItemAt(i).getUri());
                        }
                    } else if (result.getData().getData() != null) {
                        uriList.add(result.getData().getData());
                    }

                    for (Uri uri : uriList) {
                        uploadImageForChapter(uri);
                    }
                }
            });

    private void uploadImageForChapter(Uri uri) {
        ImageItem item = new ImageItem(uri.toString());
        imageItems.add(item);
        if (imagePreviewAdapter != null) {
            imagePreviewAdapter.notifyItemInserted(imageItems.size() - 1);
        }

        uploadRepository.uploadImage(uri, "chapter_pages").enqueue(
                new Callback<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> call,
                                           @NonNull Response<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> response) {
                        if (!isAdded()) return;
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            String cloudUrl = response.body().getData().imageUrl;
                            requireActivity().runOnUiThread(() -> {
                                int idx = imageItems.indexOf(item);
                                if (idx != -1) {
                                    item.cloudUrl = cloudUrl;
                                    item.uploaded = true;
                                    if (imagePreviewAdapter != null) {
                                        imagePreviewAdapter.notifyItemChanged(idx);
                                    }
                                }
                            });
                        } else {
                            requireActivity().runOnUiThread(() -> {
                                int idx = imageItems.indexOf(item);
                                if (idx != -1) {
                                    item.failed = true;
                                    if (imagePreviewAdapter != null) {
                                        imagePreviewAdapter.notifyItemChanged(idx);
                                    }
                                }
                                Toast.makeText(requireContext(), "Upload ảnh thất bại!", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<com.ptithcm.manga.data.model.response.ApiResponse<UploadResponse>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            int idx = imageItems.indexOf(item);
                            if (idx != -1) {
                                item.failed = true;
                                if (imagePreviewAdapter != null) {
                                    imagePreviewAdapter.notifyItemChanged(idx);
                                }
                            }
                            Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    // ========= Model =========
    static class ImageItem {
        String previewUrl;
        String cloudUrl;
        boolean uploaded = false;
        boolean failed = false;

        ImageItem(String previewUrl) {
            this.previewUrl = previewUrl;
        }
    }

    // ========= Adapter Preview Ảnh =========
    private static class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.VH> {
        private final List<ImageItem> items;
        private final OnRemoveListener listener;

        interface OnRemoveListener {
            void onRemove(String previewUrl);
        }

        ImagePreviewAdapter(List<ImageItem> items, OnRemoveListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            float density = parent.getContext().getResources().getDisplayMetrics().density;
            int sizePx = (int) (80 * density);

            android.widget.FrameLayout frame = new android.widget.FrameLayout(parent.getContext());
            frame.setLayoutParams(new ViewGroup.LayoutParams(sizePx + 16, sizePx + 16));

            ImageView iv = new ImageView(parent.getContext());
            android.widget.FrameLayout.LayoutParams ivLp =
                    new android.widget.FrameLayout.LayoutParams(sizePx, sizePx);
            ivLp.setMargins(8, 8, 8, 8);
            iv.setLayoutParams(ivLp);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setBackgroundColor(0xFFE0E0E0);
            frame.addView(iv);

            // Nút Xoá ở góc
            ImageView btnRemove = new ImageView(parent.getContext());
            android.widget.FrameLayout.LayoutParams btnLp = 
                    new android.widget.FrameLayout.LayoutParams((int)(20 * density), (int)(20 * density));
            btnLp.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
            btnRemove.setLayoutParams(btnLp);
            btnRemove.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            btnRemove.setBackgroundColor(0x88000000); // Nền mờ cho dễ nhìn
            btnRemove.setPadding(4, 4, 4, 4);
            frame.addView(btnRemove);

            return new VH(frame, iv, btnRemove);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ImageItem item = items.get(position);

            Glide.with(holder.iv.getContext())
                    .load(android.net.Uri.parse(item.previewUrl))
                    .placeholder(R.drawable.bg_placeholder_cover)
                    .error(R.drawable.bg_placeholder_cover)
                    .into(holder.iv);

            if (item.failed) {
                holder.iv.setAlpha(0.4f);
            } else if (!item.uploaded) {
                holder.iv.setAlpha(0.65f);
            } else {
                holder.iv.setAlpha(1.0f);
            }

            // Click vào dấu X để xoá
            holder.btnRemove.setOnClickListener(v -> listener.onRemove(item.previewUrl));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            ImageView iv;
            ImageView btnRemove;

            VH(@NonNull View itemView, ImageView iv, ImageView btnRemove) {
                super(itemView);
                this.iv = iv;
                this.btnRemove = btnRemove;
            }
        }
    }

    // ========= Adapter Danh Sách Chapter (có nút Sửa / Xoá) =========
    private class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chapter_manage, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ChapterResponse ch = chapterList.get(position);

            String label = "Ch." + com.ptithcm.manga.util.ChapterFormatter.format(ch.getChapterNumber());
            holder.tvNumber.setText(label);

            if (ch.getTitle() != null && !ch.getTitle().isEmpty()) {
                holder.tvTitle.setText(ch.getTitle());
                holder.tvTitle.setVisibility(View.VISIBLE);
            } else {
                holder.tvTitle.setVisibility(View.GONE);
            }

            // Định dạng ngày
            if (ch.getCreatedAt() != null) {
                try {
                    String dateStr = ch.getCreatedAt();
                    if (dateStr != null && dateStr.length() >= 10) {
                        String datePart = dateStr.substring(5, 10); // MM-dd
                        holder.tvDate.setText(datePart.replace("-", "/"));
                    }
                } catch (Exception e) {
                    holder.tvDate.setText("");
                }
            } else {
                holder.tvDate.setText("");
            }

            // Nút bút chì - Sửa
            holder.btnEdit.setOnClickListener(v -> showChapterDialog(ch));

            // Nút thùng rác - Xoá
            holder.btnDelete.setOnClickListener(v -> deleteChapter(ch.getId()));
        }

        @Override
        public int getItemCount() {
            return chapterList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvNumber, tvTitle, tvDate;
            ImageButton btnEdit, btnDelete;

            VH(@NonNull View itemView) {
                super(itemView);
                tvNumber = itemView.findViewById(R.id.tv_chapter_number);
                tvTitle = itemView.findViewById(R.id.tv_chapter_title);
                tvDate = itemView.findViewById(R.id.tv_date);
                btnEdit = itemView.findViewById(R.id.btn_edit_chapter);
                btnDelete = itemView.findViewById(R.id.btn_delete_chapter);
            }
        }
    }
}
