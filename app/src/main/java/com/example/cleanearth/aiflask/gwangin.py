from flask import Flask, request, jsonify
from PIL import Image
from ultralytics import YOLO
import io

app = Flask(__name__)
model = YOLO('Trash_model_yolov8s.pt')

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({'error': '이미지가 없습니다.'}), 400

    file = request.files['image']
    img = Image.open(file.stream)

    # YOLO 모델로 예측
    results = model(img)
    labels = results[0].names
    boxes = results[0].boxes

    if boxes is None or len(boxes) == 0:
        return jsonify({'label': 'none', 'confidence': 0.0})

    confidences = boxes.conf
    class_ids = boxes.cls

    if len(class_ids) == 0:
        return jsonify({'label': 'none', 'confidence': 0.0})

    # 가장 높은 신뢰도의 결과 반환
    top_class = int(class_ids[0])
    top_label = labels[top_class]
    top_confidence = float(confidences[0])

    return jsonify({
        'label': top_label,
        'confidence': round(top_confidence, 2)
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5005)  # 포트 5002로 변경
    #app.run(host='0.0.0.0', port=5001)